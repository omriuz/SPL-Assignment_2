package bgu.spl.mics.application.objects;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class CPUTest {
    public CPU cpu;
    public DataBatch dataBatch;
    int ticksToProcess;

    @Before
    public void setUp(){
        cpu = new CPU(16);
        Data data = new Data(Data.Type.Text, 20000);
        dataBatch = new DataBatch(data,0);
        ticksToProcess = (32/16)*2;
        cpu.addDataBatch(dataBatch);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAddDataBatch(){
        assertTrue(cpu.getTicksToProcess() == ticksToProcess);
        assertTrue(cpu.getDataBatch() == dataBatch);
    }

    @Test
    public void testFinishProcess(){
        for (int i=0; i<ticksToProcess; i++){
            cpu.process();
        }
        assertTrue(cpu.finishProcess());
    }

    @Test
    public void testProcess() {
        int count = cpu.getCount();
        int tickCounter = cpu.getProcessTickCounter();
        cpu.process();
        assertTrue(count+1 == cpu.getCount());
        assertTrue(tickCounter+1 == cpu.getProcessTickCounter());
    }

    @Test
    public void testFinish() {
        cpu.process();
        GPU gpu = new GPU("RTX3090");
        cpu.getDataBatch().setGpu(gpu);
        cpu.getCluster().addGPU(gpu);
        cpu.finish();
        assertTrue(cpu.getDataBatch() == null);
        assertTrue(cpu.getProcessTickCounter() == 0);
    }

}