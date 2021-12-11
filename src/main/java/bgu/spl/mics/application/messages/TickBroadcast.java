package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private String broadcastName;

    public TickBroadcast(String broadcastName) {
        this.broadcastName = broadcastName;
    }

    public String getbroadcastName() {
        return broadcastName;
    }
    
}
