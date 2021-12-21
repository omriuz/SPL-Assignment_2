package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.services.GPUService;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class GPUTest extends TestCase{
    public GPU gpu;
    public CPU cpu;
    public Cluster cluster;
    private TrainModelEvent trainModel;
    private TestModelEvent testModel;
    private GPUService service;

    public void setUp() throws Exception {
        cluster = Cluster.getInstance();
        gpu=new GPU("RTX3090");
        service = new GPUService("serve",gpu);
        cpu = new CPU(32);
        trainModel = new TrainModelEvent(new Model("omri","Images",1000));
        testModel = new TestModelEvent(new Model("Yonatan","Images",1000));
        List<Model> list = new LinkedList<>();
        list.add(testModel.getModel());
        Student student = new Student("omri","Compter science","MSc",list);

    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        service.getBus().unregister(service);
    }

    public void testSendDataBatch() {
        gpu.receiveTrainModel(trainModel);
        assertTrue(gpu.getModel()!=null && gpu.getCluster()!=null);
        int firstSize = gpu.getNumberOfBatchesSent();
        gpu.sendDataBatch();
        assertTrue(gpu.getNumberOfBatchesSent()==firstSize+1);
    }
    public void testReceiveDataBatch() {
        gpu.receiveTrainModel(trainModel);
        gpu.sendDataBatch();
        cluster.sendDataBatchToCPU(cpu);
        cpu.finish();
        cluster.sendDataBatchToGPU(gpu);
        gpu.receiveDataBatch();
        assertTrue(gpu.getSizeOfProcessedData()<=gpu.getMemoryLimit());
    }
    public void testTrainModel() {
        gpu.receiveTrainModel(trainModel);
        gpu.sendDataBatch();
        cluster.sendDataBatchToCPU(cpu);
        cpu.finish();
        gpu.receiveDataBatch();
        int size = gpu.getSizeOfProcessedData();
        gpu.trainModel();
        assertEquals(size - 1,gpu.getSizeOfProcessedData());
    }
    public void testCompleteTraining() {
        service.setCurrentEvent(trainModel);
        gpu.receiveTrainModel(trainModel);
        gpu.sendDataBatch();
        cluster.sendDataBatchToCPU(cpu);
        cpu.finish();
        cluster.sendDataBatchToGPU(gpu);
        gpu.receiveDataBatch();
        gpu.trainModel();
        gpu.completeTraining();
        assertNull(gpu.getModel());
        assertNull(gpu.getData());
        assertSame(gpu.getStatus(), GPU.Status.AVAILABLE);

    }
    public void testReceiveTrainModel() {
        assertNull(gpu.getModel());
        gpu.receiveTrainModel(trainModel);
        assertNotNull(gpu.getModel());
    }
    public void testReceiveTestModel() {
        assertTrue(testModel.getModel().getResults()== Model.Results.None);
        service.setCurrentEvent(testModel);
        gpu.receiveTestModel(testModel);
    }
}