package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    ConcurrentLinkedQueue<String> names ;
    ConfrenceInformation confrenceInformation;
    Callback<TickBroadcast> tickBroadcastCallback;
    Callback<PublishResultsEvent> publishResultsEventCallback;

    public ConferenceService(ConfrenceInformation confrenceInformation) {
        super(confrenceInformation.getName());
        this.confrenceInformation = confrenceInformation;
        this.confrenceInformation.setConferenceService(this);
        this.names = new ConcurrentLinkedQueue();
        publishResultsEventCallback =(PublishResultsEvent event)->{
            names.add(event.getModelName());
        };
        tickBroadcastCallback = (TickBroadcast tickBroadcast)->{
            if(tickBroadcast.getTime()>=this.confrenceInformation.getDate())
                sendBroadcast(new PublishConferenceBroadcast(names));
        };
    }
    @Override
    protected void initialize() {
        subscribeEvent(PublishResultsEvent.class,publishResultsEventCallback);
        subscribeBroadcast(TickBroadcast.class,tickBroadcastCallback);

    }

    public ConcurrentLinkedQueue<String> getNames() {
        return names;
    }
}
