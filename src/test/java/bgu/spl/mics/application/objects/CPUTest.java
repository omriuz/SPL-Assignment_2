package bgu.spl.mics.application.objects;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class CPUTest {
    public CPU cpu;
    Collection<DataBatch> data;

    @Before
    public void setUp(){
        cpu = new CPU(8);
        data = new LinkedList<DataBatch>();
        for (int i = 0; i < 7; i++) {
            data.add(new DataBatch());
        }
    }


    @Test
    public void testAddData() {
        assertTrue(data!=null);
        int before = cpu.getNPDataBatchesSize();
        cpu.addData(data);
        assertTrue(before + data.size() == cpu.getNPDataBatchesSize());
    }

    @Test
    public void testProcess() {
        cpu.addData(data);
        assertTrue(cpu.getNPDataBatchesSize()>0);
        cpu.process();
        assertTrue(cpu.isNPDataBatchesEmpty());
        assertFalse(cpu.isPDataBatchesEmpty());
    }

    @Test
    public void testReturnProcessedBatch() {
        assertFalse(cpu.isPDataBatchesEmpty());
        cpu.addData(data);
        cpu.process();
        int before = cpu.getPDataBatchesSize();
        Collection<DataBatch> returnedData = cpu. returnProcessedData();
        assertEquals(cpu.getPDataBatchesSize(),before + returnedData.size());
    }


}