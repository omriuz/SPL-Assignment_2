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
    private Collection<DataBatch> NPDataBatches;
    private Collection<DataBatch> PDataBatches;
    private Cluster cluster;
    private CPUService CPUService;

    public CPU (int cores_num){
        this.cores=cores_num;
    }
    public Cluster getCluster() {
        return cluster;
    }
    public int getCores() {
        return cores;
    }
    public int getNPDataBatchesSize(){return NPDataBatches.size();}
    public int getPDataBatchesSize(){return PDataBatches.size();}
    public boolean isPDataBatchesEmpty(){return PDataBatches.isEmpty();}
    public boolean isNPDataBatchesEmpty(){return NPDataBatches.isEmpty();}

    /**
    * @pre @param data != null
    * @post @pre NPDataBatches.size()+ @param data.size()==this.NPDataBatches.size()
    */
    public void addData(Collection<DataBatch> data){throw new NotImplementedException();}
    /**
     * @pre this.NPDataBatches.size() > 0
     * @post this.NPDataBatches.isEmpty() && !this.PDataBatches.isEmpty()
     */
    public void process(){throw new NotImplementedException();}

    /**
     * @pre !PDataBatches.isEmpty()
     * @post this.PDataBatches.size() = @pre PDataBatches.size()- @return.size()
     */
    public Collection<DataBatch> returnProcessedData(){throw new NotImplementedException();}





}
