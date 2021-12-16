package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import java.awt.*;

public class PublishResultsEvent implements Event<String> {
    private String modelName;

    public PublishResultsEvent(String modelName){
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }
}
