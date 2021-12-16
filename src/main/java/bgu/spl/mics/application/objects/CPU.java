package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CPUService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.util.Collection;
import java.util.Queue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private DataBatch dataBatch;
    private  Cluster cluster;
    private int processTickCounter;
    private int ticksToProcess;
//    private CPUService CPUService;

    public CPU (int cores_num){
        this.cores=cores_num;
        this.dataBatch = null;
        this.cluster = Cluster.getInstance();
        this.processTickCounter = 0;
    }
    public CPU(){}
    public Cluster getCluster() {
        return cluster;
    }
    public int getCores() {
        return cores;
    }
    public boolean isdataBatchEmpty(){
        return dataBatch == null;
    }


    private void setTicksToProcess(){
        int factor = 32/cores;
        ticksToProcess = factor*DataTypeToInt(dataBatch.getData().getType());

    }
    /**
    * @pre @param data != null
    * @post @pre NPDataBatches.size()+ @param data.size()==this.NPDataBatches.size()
    */
    public void addDataBatch(DataBatch dataBatch){
        this.dataBatch = dataBatch;
        setTicksToProcess();
    }
    /**
     * @pre this.NPDataBatches.size() > 0
     * @post this.NPDataBatches.isEmpty() && !this.PDataBatches.isEmpty()
     */
    private int DataTypeToInt(Data.Type dataType){
        int ans = 0;
        if(dataType == Data.Type.Images) ans = 4;
        else if (dataType == Data.Type.Text) ans = 2;
        else ans = 1;
        return ans;
    }
    private boolean finishProcess(){
        return processTickCounter >= ticksToProcess;
    }

    public void process(){
        processTickCounter++;
        if(finishProcess()){
            cluster.addProcessedData(dataBatch);
            dataBatch = null;
            processTickCounter = 0;
        }
    }

    /**
     * @pre !PDataBatches.isEmpty()
     * @post this.PDataBatches.size() = @pre PDataBatches.size()- @return.size()
     */
    public Collection<DataBatch> returnProcessedData(){throw new NotImplementedException();}

    public void setCores(int cores) {
        this.cores = cores;
    }
}
