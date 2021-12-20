package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConferenceInformation;

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
    ConferenceInformation conferenceInformation;
    Callback<TickBroadcast> tickBroadcastCallback;
    Callback<PublishResultsEvent> publishResultsEventCallback;

    public ConferenceService(ConferenceInformation conferenceInformation) {
        super(conferenceInformation.getName());
        this.conferenceInformation = conferenceInformation;
        this.conferenceInformation.setConferenceService(this);
        publishResultsEventCallback =(PublishResultsEvent event)->{
            this.conferenceInformation.addName(event.getModelName());
        };
        tickBroadcastCallback = (TickBroadcast tickBroadcast)->{
            if(tickBroadcast.getTime()>=this.conferenceInformation.getDate()) {
                System.out.println(getName() +" has published results");
                sendBroadcast(new PublishConferenceBroadcast(this.conferenceInformation.getNames()));
                terminate();
            }
        };
    }
    @Override
    protected void initialize() {
        subscribeEvent(PublishResultsEvent.class,publishResultsEventCallback);
        subscribeBroadcast(TickBroadcast.class,tickBroadcastCallback);
        subscribeBroadcast(TerminateBroadcast.class,(t)->{
            terminate();
        });

    }
}
