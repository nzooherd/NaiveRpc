package demo.spring;

import org.junit.Test;

public class NaiveRpcTest {

    @Test
    public void test(){
        new Thread(() -> {Server server = new Server();}).start();
        Client client = new Client();
        assert ("Hello World".equals(client.callASync()));
    }
}
