package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.GPU;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    private ConcurrentLinkedQueue<Event> events;
    private Callback<TrainModelEvent> handleTrainModel;
    private Callback<TestModelEvent> handleTestModel ;
    private Callback<TickBroadcast> handleTick;
    public GPUService(String name) {
        super("GPUService");
        events = new ConcurrentLinkedQueue<>();
        handleTrainModel = (TrainModelEvent trainModelEvent)->{
            events.add(trainModelEvent);
            if(gpu.getStatus() == GPU.Status.AVAILABLE)
                gpu.receiveTrainModel((TrainModelEvent) events.poll());
        };
        handleTestModel = (TestModelEvent testModelEvent)->{
            events.add(testModelEvent);
            if(gpu.getStatus() == GPU.Status.AVAILABLE)
                gpu.receiveTestModel((TestModelEvent) events.poll());
        };
        handleTick = gpu.getTickHandle();
    }
    @Override
    protected void initialize() {
        subscribeEvent(TrainModelEvent.class,handleTrainModel);
        subscribeEvent(TestModelEvent.class,handleTestModel);
        subscribeBroadcast(TickBroadcast.class,handleTick);
    }

}
