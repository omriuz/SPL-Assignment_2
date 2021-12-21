package bgu.spl.mics.application.messages;


import bgu.spl.mics.Broadcast;

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
