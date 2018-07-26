package demo.api;

import gq.shiwenhao.naiverpc.endpoint.ConsumerProxy;
import gq.shiwenhao.naiverpc.endpoint.ProviderPublisher;
import gq.shiwenhao.naiverpc.endpoint.ProxyFactory;
import gq.shiwenhao.naiverpc.transport.RpcFuture;
import org.junit.Test;

class Client {
    public ConsumerProxy consumerProxy;
    public Client(){
        consumerProxy = new ConsumerProxy.Builder("127.0.0.1:2181",
                "demo.api.SayHello").build();
    }

    public void syncCall(){
        SayHello hello= (SayHello)consumerProxy.callSync();
        assert("Hello You are my world".equals(hello.sayHello("You are my world")));
    }
    public void asyncCall(){
        ProxyFactory proxyFactory = consumerProxy.callAsync();
        RpcFuture rpcFuture = proxyFactory.call("sayHello", new Object[]{"word"});
        assert ("Hello word".equals((String)rpcFuture.get()));
    }

}

class Server {
    private ProviderPublisher providerPublisher;

    public Server(){
        providerPublisher = new ProviderPublisher.Builder("127.0.0.1:2181", 9000,
                SayHello.class, new SayHelloImpl()).build();

    }

}

public class NaiveRpcTest {

    @Test
    public void rpcTest(){
        new Thread(()-> {Server server = new Server();}).start();
        Client client = new Client();
        client.syncCall();
        client.asyncCall();
        return;

    }
}
