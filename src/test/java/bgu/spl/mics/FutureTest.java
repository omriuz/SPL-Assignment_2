package bgu.spl.mics;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.concurrent.TimeUnit;
class FutureTest extends TestCase {
    Future<Integer> future;
    @BeforeEach
    public void setUp() {
        future = new Future<Integer>();
    }


    @org.junit.jupiter.api.Test
    void testGet1() throws InterruptedException{
        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(1100);
            } catch (InterruptedException exception) {
            }
            future.resolve(10) ;
        });
        t1.start();
        long currTime =System.currentTimeMillis();
        int result=future.get();
        currTime=System.currentTimeMillis()-currTime;
        assertTrue(currTime>1100);
        assertEquals(result,10);
    }

    @org.junit.jupiter.api.Test
    void resolveT() {
        future.resolve(10);
        assertTrue(future.isDone());
        assertTrue(future.get()==10);
    }

    @org.junit.jupiter.api.Test
    void isDoneTest() {
        assertFalse(future.isDone());
        future.resolve(0);
        assertTrue(future.isDone());
    }

    @org.junit.jupiter.api.Test
    void testGet2() {
        assertNull(future.get(100, TimeUnit.MILLISECONDS));
        assertFalse(future.isDone());
        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            future.resolve(10) ;
        });
        t1.start();
        assertNull(future.get(100, TimeUnit.MILLISECONDS));
        assertTrue(future.get(1500, TimeUnit.MILLISECONDS)==10);
        assertTrue(future.isDone());
    }
}