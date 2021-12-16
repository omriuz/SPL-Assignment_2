package bgu.spl.mics.application.messages;


import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PublishConferenceBroadcast implements Broadcast {

    private ConcurrentLinkedQueue<String> modelNames;

    public PublishConferenceBroadcast(ConcurrentLinkedQueue<String> names){
        this.modelNames = names;
    }

    public ConcurrentLinkedQueue<String> getModelsToPublish(){
        return this.modelNames;
    }

}
