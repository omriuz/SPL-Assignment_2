package bgu.spl.mics.application.objects;

import bgu.spl.mics.Callback;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
    public enum Status {AVAILABLE, TRAINING, TESTING}
    private final Type type;
    private Status status;
    private Model model;
    private Data data;
    private final Cluster cluster;
    private int tickCounter;
    private final int memoryLimit;
    private final int speed;
    private int numberOfBatchesSent;
    private BlockingQueue<DataBatch> processedData;
    private DataBatch curr;
    /**
     * constructor of GPU
     * @param type is the type of the GPU.
     * @param cluster is the cluster of the GPU.
     */
    public GPU(Type type,Cluster cluster){
        this.type = type;
        this.cluster = cluster;
        this.status = Status.AVAILABLE;
        if(this.type == Type.RTX2080) {memoryLimit = 16;speed = 2;}
        else if (this.type == Type.RTX3090) {memoryLimit = 32;speed=1;}
        else {memoryLimit = 8 ; speed=4;}
        processedData = new LinkedBlockingQueue<>(memoryLimit);
    }

    /**
     * this method checks weather the memory limit of the gpu is reached.
     * @return @boolean true if the limit is reached and @boolean false otherwise.
     * @pre this.processedData!=null
     * @post @return == this.memoryLimit == this.processedData.size()
     */
    public boolean isFull(){return processedData.size()==memoryLimit;}

    /**
     * sends the dataBatch from the trainModel to the cluster
     * @pre data!=null && cluster!=null
     * @post unProcessedData.size() = @pre unProcessedData.size() - @return.size()
     */
    public void sendDataBatch(){
        cluster.sendDataBatchToCluster(new DataBatch(this.data,1000*numberOfBatchesSent,this));
        numberOfBatchesSent++;
    }
    /**
     * @pre isFull()==false
     * @post: this.processedData.size() <= memoryLimit
     */
    public void receiveDataBatch(){
        if(cluster.isThereDataBatch(this))
            processedData.add(cluster.sendDataBatchToGPU(this));
    }
    /**
     * this function used the processed data it contains to train the model.
     * @pre this.model!=null && !isProcessedDataEmpty()
     * @post: isProcessedDataEmpty() && numberOfBatchesTrained = @pre numberOfBatchesTrained+ @pre this.processedData.size()
     */
    public void trainModel(){
        if(tickCounter == 0)
            if(!isProcessedDataEmpty()) {
                try{
                curr = processedData.take();
                }
                catch (InterruptedException e){}
            }
        else if(tickCounter ==speed){
            data.increaseProcessed();
            curr = null;
            tickCounter = 0;
            }
    }
    /**
     * called when the gpu finished training the model
     * @pre this.numberOfBatchesToProcess == numberOfBatchesProcess
     * @post the GPUService is informed that the training is finished
     */
    public void completeTraining(){
        status = Status.AVAILABLE;
        model = null;
        data = null;
        tickCounter = 0;
    }
    /**
     * gets the trainModel from the GPUService.
     * @param model is the model the gpu will be processing.
     * @pre this.data == null
     * @post this.data !=null
     */
    public void receiveTrainModel(TrainModelEvent trainModelEvent){
        this.model = trainModelEvent.getModel();
        this.data = model.getData();
        numberOfBatchesSent = 0;
        tickCounter = 0;
        status = Status.TRAINING;
    }
    /**
     * @post:   model.getStatus()=="Tested" && (model.getResults()=="good" || model.getResults()=="bad")
     * @param model
     */
    public void receiveTestModel(TestModelEvent testModelEvent){throw new NotImplementedException();}
    public void receiveTickBroadcast(TickBroadcast tickBroadcast){
        if(status==Status.TRAINING){
            tickCounter++;
            if((data.getProcessed()<data.getSize())) {
                sendDataBatch();
                receiveDataBatch();
                trainModel();
            }
            else
                completeTraining();
        }
        if(status == Status.TESTING){

        }

    }
    public Cluster getCluster(){return this.cluster;}
    public int getSizeOfProcessedData(){return this.processedData.size();}
    public int getMemoryLimit(){return this.memoryLimit;}
    public boolean isProcessedDataEmpty(){return processedData.isEmpty();}
    public Status getStatus() {
        return status;
    }
    public Callback<TickBroadcast> getTickHandle(){
        return this::receiveTickBroadcast;
    }
}
