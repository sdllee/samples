package me.leon.samples.zookeeper;

import me.leon.samples.redis.EventNumSupplier;
import me.leon.samples.utils.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;


@RunWith(SpringRunner.class)
@SpringBootTest
public class EventNumZkSupplierTest {


    @Autowired
    private EventNumZkSupplier numSupplier;
    @Autowired
    private ZkClient zkClient;

    @Before
    public void initNodeData() {
        numSupplier.initData();
    }

    @Test
    public void testGetEventNum() {
        CountDownLatch latch = new CountDownLatch(4);
        Runnable run = () -> {
            for(int i = 0; i < 1000; i ++) {
                String eventNum = numSupplier.getEventNum(Thread.currentThread().getName());
                System.out.println(Thread.currentThread().getName()  + ", eventNum=" + eventNum);
            }
            latch.countDown();
        };

        Thread thread1 = new Thread(run, "thread1");
        Thread thread2 = new Thread(run, "thread2");
        Thread thread3 = new Thread(run, "thread3");
        Thread thread4 = new Thread(run, "thread4");
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
