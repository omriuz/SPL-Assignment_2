package bgu.spl.mics.application.messages;


import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;
import java.util.List;

public class PublishConferenceBroadcast implements Broadcast {
    private List<String> modelsToPublish;

    public PublishConferenceBroadcast(){
        this.modelsToPublish = new LinkedList<>();
    }

    public List<String> getModelsToPublish(){
        return this.modelsToPublish;
    }

}
