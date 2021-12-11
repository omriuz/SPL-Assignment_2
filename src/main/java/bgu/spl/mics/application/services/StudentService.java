package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
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

    public StudentService(String name, Student student) {
        super(name);
        this.student = student;
    }
    /*
        protected final <T, E extends Event<T>> void subscribeEvent(Class<E> type, Callback<E> callback) {
        callbacksMap.putIfAbsent(type,callback);
        bus.subscribeEvent(type,this);
    }
     */
    private void subscribe(){
        subscribeBroadcast(PublishConferenceBroadcast.class, (message)->{
            LinkedList<Model> publishedModels = (LinkedList<Model>) message.getModelsToPublish();
            for(Model m : publishedModels){
                if(student.getModels().contains(m)){
                    student.incPublications();
                }else{
                    student.incPapersRead();
                }
            }
        });

        subscribeBroadcast(TickBroadcast.class, t->{ // TODO general callbeck for the TickBroadcast

        });
    }

    @Override
    protected void initialize() {
        subscribe();
    }
}
