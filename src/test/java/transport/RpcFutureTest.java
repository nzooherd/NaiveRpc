package transport;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RpcFutureTest {

    @Test
    public void timeoutTest() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean flag = countDownLatch.await(2000, TimeUnit.MILLISECONDS);
                    assert (!flag);
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        countDownLatch.await();
    }
}
