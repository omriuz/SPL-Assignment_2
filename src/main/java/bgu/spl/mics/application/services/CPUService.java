package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;


/**
 * CPU service is responsible for handling the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {

    private CPU cpu;


    public CPUService(String name, CPU cpu) {
        super(name);
        this.cpu = cpu;
        // TODO Implement this
    }

    protected void subscribe(){
        subscribeBroadcast(TerminateBroadcast.class,(t)->{
            terminate();
        });
        subscribeBroadcast(TickBroadcast.class, t->{
            if(cpu.isDataBatchEmpty()){
                cpu.getCluster().sendDataBatchToCPU(cpu);
            }
            cpu.process();
        });
    }

    @Override
    protected void initialize() {
        subscribe();
    }
}
