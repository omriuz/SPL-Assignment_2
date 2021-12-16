package bgu.spl.mics.application.objects;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.services.GPUService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Random;
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
    public enum Type {RTX3090, RTX2080, GTX1080}
    public enum Status {AVAILABLE, TRAINING}
    private  Type type;
    private Status status;
    private Model model;
    private Data data;
    private  Cluster cluster;
    private int tickCounter;
    private  int memoryLimit;
    private  int speed;
    private int numberOfBatchesSent;
    private BlockingQueue<DataBatch> processedData;
    private DataBatch curr;
    private GPUService service;
    /**
     * constructor of GPU
     * @param type is the type of the GPU.
     * @param cluster is the cluster of the GPU.
     */
    public GPU(){}

    public GPU(String type){
        this.type = stringToType(type);
        this.cluster = Cluster.getInstance();
        this.cluster.addGPU(this);
        this.status = Status.AVAILABLE;
        if(this.type == Type.RTX2080) {memoryLimit = 16;speed = 2;}
        else if (this.type == Type.RTX3090) {memoryLimit = 32;speed=1;}
        else {memoryLimit = 8 ; speed=4;}
        processedData = new LinkedBlockingQueue<>(memoryLimit);
    }

    private Type stringToType(String sType){
        Type type = null;
        if(sType == "RTX3090")
            type = Type.RTX3090;
        else if(sType == "RTX2080")
            type = Type.RTX2080;
        else if(sType == "GTX1080")
            type = Type.GTX1080;
        return type;
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
        if(cluster.isThereDataBatch(this) && processedData.size()<memoryLimit)
            processedData.add(cluster.sendDataBatchToGPU(this));
    }
    /**
     * this function used the processed data it contains to train the model.
     * @pre this.model!=null && !isProcessedDataEmpty()
     * @post: isProcessedDataEmpty() && numberOfBatchesTrained = @pre numberOfBatchesTrained+ @pre this.processedData.size()
     */
    public void trainModel(){
        if(tickCounter == 0) {
            if (!isProcessedDataEmpty()) {
                try {
                    curr = processedData.take();
                    tickCounter++;
                } catch (InterruptedException e) {
                }
            }
        }
        else tickCounter++;
        if(tickCounter == speed){
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
        System.out.println("completed training - " +  model.getName());
        status = Status.AVAILABLE;
        tickCounter = 0;
        model.setStatus(Model.Status.Trained);
        model.getStudent().addTrainedModel(model);
        model = null;
        data = null;
        service.completeTrain();
    }
    /**
     * gets the trainModel from the GPUService.
     * @param trainModelEvent is the TrainModelEvent the gpu will be processing.
     * @pre this.data == null
     * @post this.data !=null
     */
    public void receiveTrainModel(TrainModelEvent trainModelEvent){
        this.model = trainModelEvent.getModel();
        System.out.println("Received train model: " +  model.getName());
        this.data = model.getData();
        numberOfBatchesSent = 0;
        tickCounter = 0;
        status = Status.TRAINING;
        model.setStatus(Model.Status.Training);
    }
    /**
     * @post:   model.getStatus()=="Tested" && (model.getResults()=="good" || model.getResults()=="bad")
     * @param
     */
    public void receiveTestModel(TestModelEvent testModelEvent){
        System.out.println("Received test model - " + model.getName());
        Student.Degree degree = testModelEvent.getModel().getStudent().getDegStatus();
        Random rand = new Random();
        double num = rand.nextDouble();
        boolean success = degree == Student.Degree.MSc ? num>=0.6 : num>=0.8;
        testModelEvent.getModel().setStatus(Model.Status.Tested);
        Model.Results result = success ? Model.Results.Good: Model.Results.Bad;
        if(result== Model.Results.Good)
            testModelEvent.getModel().getStudent().addPublishedModel(testModelEvent.getModel());
        service.completeTest(result);

    }
    public void receiveTickBroadcast(TickBroadcast tickBroadcast){
        if(status == Status.AVAILABLE){
            Event e = service.getTaskFromQueue();
            if(e.getClass()==TrainModelEvent.class){
                receiveTrainModel((TrainModelEvent) e);
            }
            else if(e.getClass() == TestModelEvent.class){
                receiveTestModel((TestModelEvent) e);
            }
        }
        else if(status==Status.TRAINING){
            if((data.getProcessed()<data.getSize())) {
                sendDataBatch();
                receiveDataBatch();
                trainModel();
            }
            else
                completeTraining();
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
        return (TickBroadcast tickBroadcast)->{receiveTickBroadcast(tickBroadcast);};
    }
    public void setService(GPUService service){
        this.service = service;
    }

    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
}
