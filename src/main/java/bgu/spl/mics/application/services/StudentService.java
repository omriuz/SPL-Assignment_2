package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    Student student;
    Model currentModel;
    boolean noMoreTicks;
    public StudentService(String name, Student student) {
        super(name);
        this.student = student;
        this.currentModel = student.getNextModel();
        this.noMoreTicks = false;
    }

    private Event<Boolean> buildTrain(){
        Event event = new TrainModelEvent(currentModel);
        return event;
    }
    private void sendTrain(){
        currentModel.setStatus(Model.Status.Training);
        student.setFuture(getBus().sendEvent(buildTrain()));
    }

    private Event<Model.Results> buildTest(){
        Event event = new TestModelEvent(currentModel);
        return event;
    }

    private void sendTest(){
        student.setFuture(getBus().sendEvent(buildTest()));
    }

    private void sendResult(){
        student.addPublishedModel(currentModel);
        getBus().sendEvent(buildResult());
    }
    private Event<Boolean> buildResult(){
        Event publishResult = new PublishResultsEvent(currentModel.getName());
        return publishResult;
    }
    private void subscribe(){
        subscribeBroadcast(PublishConferenceBroadcast.class, (message)->{
            ConcurrentLinkedQueue<String> publishedModels = message.getModelsToPublish();
            for(String modelName : publishedModels){
                if(student.getPublishedModelNames().contains(modelName)){
                    student.incPublications();
                }else{
                    student.incPapersRead();
                }
            }
        });
        subscribeBroadcast(TerminateBroadcast.class,(t)-> terminate());
        subscribeBroadcast(TickBroadcast.class, t->{
            if(noMoreTicks);
            //TODO: maybe to add unsubscribe to ticks if finished
            else if(student.isFinished()) {
                MessageBusImpl impl = (MessageBusImpl) getBus();
                impl.unsubscribeBroadcast(TickBroadcast.class, this);
                noMoreTicks = true;
            }
            else if(currentModel.getStatus() == Model.Status.PreTrained){
                sendTrain();
                currentModel.setStatus(Model.Status.Training);
            }
            else if(currentModel.getStatus() == Model.Status.Training){
                Boolean trainResult = (Boolean)student.getFuture().get(1, TimeUnit.MILLISECONDS); // need to be timed get()
                if(trainResult!=null){
                    currentModel.setStatus(Model.Status.Trained);
                    student.addTrainedModel(currentModel);
                    sendTest();
                }
            }else if(currentModel.getStatus() == Model.Status.Trained){
                Object testResult = student.getFuture().get();
                if(testResult!=null) {
                    Model.Results result ;
                    if((Boolean)testResult)
                        result = Model.Results.Good;
                    else
                        result = Model.Results.Bad;
                    currentModel.setStatus(Model.Status.Tested);
                    currentModel.setResults(result);
                    if (result == Model.Results.Good) {
                        currentModel.getStudent().addPublishedModel(currentModel);
                        sendResult();
                    }
                    currentModel = student.getNextModel();
                }
                else {
                    terminate();
                }
            }
        });
    }

    @Override
    protected void initialize() {
        subscribe();
    }
}
