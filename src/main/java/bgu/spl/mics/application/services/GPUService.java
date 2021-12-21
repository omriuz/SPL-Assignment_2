package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.GPU;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private final GPU gpu;
    private final Callback<TrainModelEvent> handleTrainModel;
    private final Callback<TestModelEvent> handleTestModel ;
    private final Callback<TickBroadcast> handleTick;
    private final Callback<TerminateBroadcast> handleTerminate;
    private final LinkedBlockingDeque <Event> studentEvents;
    private Event currentEvent;
    public GPUService(String name, GPU gpu) {
        super("GPUService " + name);
        this.gpu = gpu;
        gpu.setService(this);
        studentEvents = new LinkedBlockingDeque<>();
        handleTrainModel = studentEvents::addLast;
        handleTestModel = studentEvents::addFirst;
        handleTick = (tickBroadcast)->{
            if(gpu.getStatus() == GPU.Status.AVAILABLE){
                Event e;
                if(hasTaskInQueue()) {
                    e = getTaskFromQueue();
                    if (e.getClass() == TrainModelEvent.class) {
                        gpu.receiveTrainModel((TrainModelEvent) e);
                    } else if (e.getClass() == TestModelEvent.class) {
                        gpu.receiveTestModel((TestModelEvent) e);
                    }
                }
            }
            else if(gpu.getStatus() == GPU.Status.TRAINING){
                if(gpu.getNumberOfBatchesSent()*1000 < gpu.getData().getSize()) {
                    gpu.sendDataBatch();
                }
                if(gpu.getNumberOfBatchesReceived() < gpu.getNumberOfBatchesSent())
                    gpu.receiveDataBatch();
                gpu.trainModel();
                if(gpu.getData().getProcessed() >= gpu.getData().getSize())
                    gpu.completeTraining();
            }
        };
        handleTerminate = (t)->{
            gpu.getCluster().removeGPU(gpu);
            gpu.getCluster().terminate();
            terminate();
        };
    }
    @Override
    protected void initialize() {
        subscribeEvent(TrainModelEvent.class,handleTrainModel);
        subscribeEvent(TestModelEvent.class,handleTestModel);
        subscribeBroadcast(TickBroadcast.class,handleTick);
        subscribeBroadcast(TerminateBroadcast.class,handleTerminate);
    }
    public Event getTaskFromQueue(){
        try {
            this.currentEvent = studentEvents.take();
            return currentEvent;
        }
        catch (InterruptedException e){e.printStackTrace();}
        return  null;
    }
    public void completeTrain(){

        complete(currentEvent,true);
    }
    public void completeTest(Boolean result){
        complete(currentEvent,result);
    }
    public boolean hasTaskInQueue(){
        return !studentEvents.isEmpty();
    }
    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }
}
