package demo.api;

import demo.SayHello;
import demo.SayHelloImpl;
import gq.shiwenhao.naiverpc.endpoint.ConsumerProxy;
import gq.shiwenhao.naiverpc.endpoint.ProviderPublisher;
import gq.shiwenhao.naiverpc.endpoint.ProxyFactory;
import gq.shiwenhao.naiverpc.transport.RpcFuture;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class NaiveRpcTest {

    @Test
    public void rpcTest() throws InterruptedException {
        new Thread(()-> {
            Server server = new Server();
        }).start();
        Client client = new Client();
        client.syncCall();
        client.asyncCall();
    }
}
