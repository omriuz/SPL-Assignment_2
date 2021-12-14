package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import org.junit.Test;

import javax.jws.WebParam;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private GPU gpu;
    private Callback<TrainModelEvent> handleTrainModel;
    private Callback<TestModelEvent> handleTestModel ;
    private Callback<TickBroadcast> handleTick;
    private LinkedBlockingDeque <Event> studentEvents;
    private Event currentEvent;
    public GPUService(String name, GPU gpu) {
        super("GPUService");
        this.gpu = gpu;
        gpu.setService(this);
        studentEvents = new LinkedBlockingDeque<>();
        handleTrainModel = (TrainModelEvent trainModelEvent)->{
            studentEvents.addLast(trainModelEvent);
        };
        handleTestModel = (TestModelEvent testModelEvent)->{
            studentEvents.addFirst(testModelEvent);
        };
        handleTick = gpu.getTickHandle();
    }
    @Override
    protected void initialize() {
        subscribeEvent(TrainModelEvent.class,handleTrainModel);
        subscribeEvent(TestModelEvent.class,handleTestModel);
        subscribeBroadcast(TickBroadcast.class,handleTick);
    }
    public Event getTaskFromQueue(){
        try {
            currentEvent = studentEvents.take();
            return currentEvent;
        }
        catch (InterruptedException e){}
        return  null;
    }
    public void completeTrain(){
        complete((TrainModelEvent)currentEvent,true);
    }
    public void completeTest(Model.Results result){
        complete((TestModelEvent)currentEvent,result);
    }

}
