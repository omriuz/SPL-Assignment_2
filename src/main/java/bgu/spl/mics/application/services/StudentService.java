package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.LinkedList;

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
    Model curentModel;
    public StudentService(String name, Student student) {
        super(name);
        this.student = student;
        this.curentModel = student.getNextModel();
    }
    /*
        protected final <T, E extends Event<T>> void subscribeEvent(Class<E> type, Callback<E> callback) {
        callbacksMap.putIfAbsent(type,callback);
        bus.subscribeEvent(type,this);
    }
     */
    private Event<Boolean> buildTrain(){
        Event event = new TrainModelEvent(curentModel);
        return event;
    }
    private void sendTrain(){
        student.setFuture(getBus().sendEvent(buildTrain()));
    }

    private Event<Model.Results> buildTest(){
        Event event = new TestModelEvent(curentModel);
        return event;
    }

    private void sendTest(){
        student.setFuture(getBus().sendEvent(buildTest()));
    }

    private void sendResult(){
        student.addPublishedModel(curentModel.getName());
        getBus().sendEvent(buildResult());
    }
    private Event<Boolean> buildResult(){
        Event publishResult = new PublishResultsEvent(curentModel.getName());
        return publishResult;
    }
    private void subscribe(){
        subscribeBroadcast(PublishConferenceBroadcast.class, (message)->{
            LinkedList<String> publishedModels = (LinkedList<String>) message.getModelsToPublish();
            for(String modelName : publishedModels){
                if(student.getPublishedModels().contains(modelName)){
                    student.incPublications();
                }else{
                    student.incPapersRead();
                }
            }
        });

        subscribeBroadcast(TickBroadcast.class, t->{
            if(student.isFinished());
            else if(curentModel.getStatus() == Model.Status.PreTrained){
                sendTrain();
            }
            else if(curentModel.getStatus() == Model.Status.Training){
                Boolean trainResult = (Boolean)student.getFuture().get(); // need to be timed get()
                if(trainResult){
                    sendTest();
                }
            }else if(curentModel.getStatus() == Model.Status.Trained){
                Model.Results testResult = (Model.Results)student.getFuture().get();
                if(testResult == Model.Results.Good){
                    curentModel = student.getNextModel();
                    sendResult();
                }else if(testResult == Model.Results.Bad){
                    sendTrain();
                }
            }
        });
    }

    @Override
    protected void initialize() {
        subscribe();
        sendTrain();
    }
}
