package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class TrainModelEvent implements Event<Boolean> {

    private final Model model;
    private Boolean trained;
    public TrainModelEvent(Model model){
        this.model = model;
        this.trained = false;
    }
    public Model getModel(){
        return model;
    }
    public void complete(){
        trained = true;
    }
    public Boolean getTrained(){
        return trained;
    }

}
