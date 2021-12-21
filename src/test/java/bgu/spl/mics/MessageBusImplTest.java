package bgu.spl.mics;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class MessageBusImplTest {
    MessageBusImpl msgBus = MessageBusImpl.getInstance();
    GPU gpu;
    List<Model> models = new LinkedList<>();
    Student student1;
    Student student2;
    GPUService gpuServ;
    StudentService studentServ1;
    StudentService studentServ2;
    Event e;
    Broadcast b;
    Model model;

    @Before
    public void setUp() {
        model = new Model("GoodModel", "Text", 20000);
        models.add(model);
        student1 = new Student("Omri", "computerSince", "Phd", models);
        student2 = new Student("yonatan", "computerScince", "Phd", models);
        studentServ1 = new StudentService("Omri", student1);
        studentServ2 = new StudentService("Yonatan", student2);
        gpu = new GPU("RTX3090");
        gpuServ = new GPUService("service1", gpu);
        e = new ExampleEvent(student1.getName());
        b = new ExampleBroadcast("123");
        msgBus.unregister(gpuServ);
        msgBus.unregister(studentServ1);
        msgBus.unregister(studentServ2);
    }

    @After
    public void tearDown() throws Exception {
        msgBus.unregister(gpuServ);
        msgBus.unregister(studentServ1);
        msgBus.unregister(studentServ2);
    }

    @Test
    public void testGetInstance() {
        assertTrue(msgBus == MessageBusImpl.getInstance());
    }

    @Test
    public void testSubscribeEvent() {
        msgBus.register(studentServ1);
        msgBus.subscribeEvent(ExampleEvent.class, studentServ1);
        assertTrue(msgBus.isSubscribeToEvent(studentServ1, ExampleEvent.class));
    }

    @Test
    public void testSubscribeBroadcast() {
        msgBus.register(studentServ1);
        msgBus.subscribeBroadcast(ExampleBroadcast.class, studentServ1);
        assertTrue(msgBus.isSubscribeToBroadcast(studentServ1, ExampleBroadcast.class));
    }

    @Test
    public void testComplete() {
        msgBus.register(gpuServ);
        msgBus.subscribeEvent(ExampleEvent.class, gpuServ);
        Future f = msgBus.sendEvent(e);
        String result = "Finished";
        msgBus.complete(e, result);
        assertTrue(f.isDone());
        assertEquals(f.get(), result);
    }

    @Test
    public void testSendBroadcast() throws InterruptedException {
        msgBus.register(studentServ1);
        msgBus.register(studentServ2);
        msgBus.subscribeBroadcast(ExampleBroadcast.class, studentServ1);
        msgBus.subscribeBroadcast(ExampleBroadcast.class, studentServ2);
        msgBus.sendBroadcast(b);
        assertEquals(msgBus.awaitMessage(studentServ1), b);
        assertEquals(msgBus.awaitMessage(studentServ2), b);
    }

    @Test
    public void testSendEvent() throws InterruptedException {
        msgBus.register(studentServ1);
        msgBus.subscribeEvent(ExampleEvent.class, studentServ1);
        msgBus.sendEvent(e);
        Message m = msgBus.awaitMessage(studentServ1);
        assertEquals(e, m);
    }

    @Test
    public void testRegister() {
        msgBus.register(studentServ1);
        assertTrue(msgBus.isRegistered(studentServ1));
    }

    @Test
    public void testUnregister() {
        msgBus.register(studentServ1);
        msgBus.unregister(studentServ1);
        assertFalse(msgBus.isRegistered(studentServ1));
    }

    @Test
    public void testAwaitMessage() throws InterruptedException {
        msgBus.register(studentServ1);
        msgBus.subscribeBroadcast(ExampleBroadcast.class, studentServ1);
        msgBus.sendBroadcast(b);
        assertTrue(msgBus.inQueue(studentServ1,b));
        msgBus.awaitMessage(studentServ1);
        assertFalse(msgBus.inQueue(studentServ1,b));
    }
}
