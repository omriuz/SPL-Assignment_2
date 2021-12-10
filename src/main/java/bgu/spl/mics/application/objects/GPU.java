package bgu.spl.mics.application.objects;

import bgu.spl.mics.Future;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}
    private Type type;
    private Model model;
    private Cluster cluster;
    private int time;
    private int memoryLimit;
    private int numberOfBatchesToProcess;
    private int numberOfBatchesTrained;
    private Collection<DataBatch> processedData;
    private Collection<DataBatch> unProcessedData;

    public GPU(){throw new NotImplementedException();}
    /**
     * constructor of GPU
     * @param type is the type of the GPU.
     * @param cluster is the cluster of the GPU.
     */
    public GPU(Type type,Cluster cluster){throw new NotImplementedException();}

    /**
     * gets the trainModel from the GPUService.
     * @param model is the model the gpu will be processing.
     * @pre this.model == null
     * @post this.model !=null
     */
    public void receiveTrainModel(Model model){throw new NotImplementedException();}
    /**
     * this method checks weather the memory limit of the gpu is reached.
     * @return @boolean true if the limit is reached and @boolean false otherwise.
     * @pre this.processedData!=null
     * @post @return == this.memoryLimit == this.processedData.size()
     */
    public boolean isFull(){throw new NotImplementedException();}

    /**
     * sends the data from the trainModel to the cluster in a collection of @DataBatch.
     * @pre model!=null && cluster!=null
     * @post unProcessedData.size() = @pre unProcessedData.size() - @return.size()
     * @return the method returns an collection of @DataBatch that need to be processed to the cluster.
     */
    public Collection<DataBatch> sendData(){
        throw new NotImplementedException();
    }
    /**
     * checks if the gpu can receive data of @param size or not due to space
     * @param size the number of dataBatches in question
     * @pre this.memoryLimit!=0
     * @post
     */
    public boolean canReceiveData(int size){
        throw new NotImplementedException();
    }
    /**
     * @param data is a collection of data processed by the cpu's.
     * @pre canReceiveData()==true
     * @post: this.processedData.size() <= memoryLimit
     */
    public void receiveData(Collection<DataBatch> data){throw new NotImplementedException();}
    /**
     * this function used the processed data it contains to train the model.
     * @pre this.model!=null && !isProcessedDataEmpty()
     * @post: isProcessedDataEmpty() && numberOfBatchesTrained = @pre numberOfBatchesTrained+ @pre this.processedData.size()
     */
    public void trainModel(){throw new NotImplementedException();}
    /**
     * called when the gpu finished training the model
     * @pre this.numberOfBatchesToProcess == numberOfBatchesProcess
     * @post the GPUService is informed that the training is finished
     */
    public void completeTraining(){throw new NotImplementedException();}
    /**
     * @post:   model.getStatus()=="Tested" && (model.getResults()=="good" || model.getResults()=="bad")
     */
    public void receiveTestModel(Model model){throw new NotImplementedException();}
    public Model getModel(){return this.model;}
    public Cluster getCluster(){return this.cluster;}
    public int getSizeOfUnProcessedData(){return this.unProcessedData.size();}
    public int getSizeOfProcessedData(){return this.processedData.size();}
    public int getMemoryLimit(){return this.memoryLimit;}
    public boolean isProcessedDataEmpty(){return processedData.isEmpty();}
    public int getNumberOfBatchesTrained(){return this.numberOfBatchesTrained;}
    public int getNumberOfBatchesToProcess(){return this.numberOfBatchesToProcess;}




}
