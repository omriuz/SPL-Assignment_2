//package bgu.spl.mics.application.objects;
//
//import junit.framework.TestCase;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.LinkedList;
//
//public class GPUTest extends TestCase{
//    public GPU gpu;
//    public Cluster cluster;
//    private Model trainModel;
//    private Model testModel;
//
//    public void setUp() throws Exception {
//        cluster = new Cluster();
//        gpu=new GPU(GPU.Type.GTX1080,cluster);
//        trainModel = new Model();
//        testModel = new Model();
//    }
//
//    public void testReceiveTrainModel() {
//        assertTrue(gpu.getModel()==null);
//        gpu.receiveTrainModel(trainModel);
//        assertTrue(gpu.getModel()!=null);
//    }
//
//    public void testIsFull() {
//        gpu.receiveTrainModel(trainModel);
//        assertTrue(gpu.getModel()!=null&&gpu.getCluster()!=null);
//        assertTrue(gpu.isFull()==(gpu.getMemoryLimit()==gpu.getSizeOfProcessedData()));
//    }
//
//    public void testSendData() {
//        gpu.receiveTrainModel(trainModel);
//        assertTrue(gpu.getModel()!=null && gpu.getCluster()!=null);
//        int firstSize = gpu.getSizeOfUnProcessedData();
//        Collection<DataBatch> data = gpu.sendData();
//        assertTrue(gpu.getSizeOfUnProcessedData()==firstSize-data.size());
//    }
//    public void testCanReceiveData() {
//        assertTrue(gpu.getMemoryLimit()!=0);
//        assertFalse(gpu.canReceiveData(8));
//        assertTrue(gpu.canReceiveData(7));
//    }
//    public void testReceiveData() {
//        gpu.receiveTrainModel(trainModel);
//        Collection<DataBatch> data = new LinkedList<>();
//        for (int i = 0; i < 7; i++) {
//            data.add(new DataBatch());
//        }
//        assertTrue(gpu.canReceiveData(data.size()));
//        gpu.receiveData(data);
//        assertTrue(gpu.getSizeOfProcessedData()<=gpu.getMemoryLimit());
//    }
//    public void testTrainModel() {
//        gpu.receiveTrainModel(trainModel);
//        assertTrue(gpu.getModel()!=null);
//        assertFalse(gpu.isProcessedDataEmpty());
//        Collection<DataBatch> data = new LinkedList<>();
//        for (int i = 0; i < 7; i++) {
//            data.add(new DataBatch());
//        }
//        gpu.receiveData(data);
//        int before = gpu.getNumberOfBatchesTrained();
//        gpu.trainModel();
//        assertTrue(gpu.isProcessedDataEmpty());
//        assertTrue(gpu.getNumberOfBatchesTrained() == before + data.size());
//    }
//
//    public void testCompleteTraining() {
//        gpu.receiveTrainModel(trainModel);
//        Collection<DataBatch> data = new LinkedList<>();
//        for (int i = 0; i < 7; i++) {
//            data.add(new DataBatch());
//        }
//        gpu.receiveData(data);
//        gpu.trainModel();
//        assertTrue(gpu.getNumberOfBatchesToProcess()==gpu.getNumberOfBatchesTrained());
//        gpu.completeTraining();
//
//    }
//
//    public void testReceiveTestModel() {
//        gpu.receiveTestModel(testModel);
//        assertTrue(testModel.getStatus()=="Tested");
//        assertTrue(trainModel.getResults()=="good" || trainModel.getResults()=="bad");
//    }
//}