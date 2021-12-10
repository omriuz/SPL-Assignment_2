package bgu.spl.mics;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MessageBusImplTest {
    MessageBus msgBus = MessageBusImpl.getInstance();
    GPUService gpuServ = new GPUService("service1");
    StudentService student1 = new StudentService("Dani");
    StudentService student2 = new StudentService("Josh");
    ExampleEvent e = new ExampleEvent("student 1");
    ExampleBroadcast b = new ExampleBroadcast("2");
    @Before
    public void setUp() {
        msgBus.unregister(gpuServ);
        msgBus.unregister(student1);
        msgBus.unregister(student2);
    }

    @Test
    public void TestSubscribeEvent() {
        msgBus.register(gpuServ);
        msgBus.subscribeEvent(ExampleEvent.class, gpuServ);
        int serviceId = gpuServ.getId();
        assertTrue(msgBus.isSubscribeToEvent(serviceId, ExampleEvent.class));
    }

    @Test
    public void testSubscribeBroadcast() {
        msgBus.register(gpuServ);
        msgBus.subscribeBroadcast(ExampleBroadcast.class, gpuServ);
        int serviceId = gpuServ.getId();
        assertTrue(msgBus.isSubscribeToBroadcast(serviceId, ExampleBroadcast.class));
    }

    @Test
    public void testComplete() {
        msgBus.register(gpuServ);
        msgBus.subscribeEvent(ExampleEvent.class, gpuServ);
        Future<String> f =  msgBus.sendEvent(e);
        String result = "Finished";
        msgBus.complete(e,result);
        assertTrue(f.isDone());
        assertEquals(f.get(),result);
    }

    @Test
    public void testSendBroadcast() throws InterruptedException{
        msgBus.register(student1);
        msgBus.register(student2);
        msgBus.subscribeBroadcast(ExampleBroadcast.class, student1);
        msgBus.subscribeBroadcast(ExampleBroadcast.class, student2);
        msgBus.sendBroadcast(b);
        assertEquals(msgBus.awaitMessage(student1),b);
        assertEquals(msgBus.awaitMessage(student2),b);
    }

    @Test
    void testSendEvent() throws InterruptedException {
        msgBus.register(gpuServ);
        msgBus.subscribeEvent(ExampleEvent.class, gpuServ);
        msgBus.sendEvent(e);
        Message m = msgBus.awaitMessage(gpuServ);
        assertEquals(e,m);
    }

    @Test
    void testRegister() {
        msgBus.register(student1);
        assertNotEquals(student1.getId(), -1);
        assertTrue(msgBus.isRegister(student1.getId()));
    }

    @Test
    void testUnregister() {
        msgBus.register(student1);
        msgBus.unregister(student1);
        assertFalse(msgBus.isRegister(student1.getId()));
    }

    @Test
    void testAwaitMessage() throws InterruptedException {
        msgBus.register(student1);
        msgBus.subscribeBroadcast(ExampleBroadcast.class, student1);
        msgBus.sendBroadcast(b);
        assertTrue(msgBus.inQueue(student1.getId(),b));
        msgBus.awaitMessage(student1);
        assertFalse(msgBus.inQueue(student1.getId(),b));
    }
}