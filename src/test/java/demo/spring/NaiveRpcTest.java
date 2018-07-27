package demo.spring;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class NaiveRpcTest {

    public void test() throws InterruptedException {
        new Thread(() -> {
            Server server = new Server();
       }, "Server").start();

        Client client = new Client();
        assert ("Hello World".equals(client.callASync()));
    }
}
