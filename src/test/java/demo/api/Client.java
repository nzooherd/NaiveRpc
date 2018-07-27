package demo.api;

import demo.SayHello;
import gq.shiwenhao.naiverpc.endpoint.ConsumerProxy;
import gq.shiwenhao.naiverpc.endpoint.ProxyFactory;
import gq.shiwenhao.naiverpc.transport.RpcFuture;

public class Client {
    public ConsumerProxy consumerProxy;
    public Client(){
        consumerProxy = new ConsumerProxy.Builder("127.0.0.1:2181",
                "demo.SayHello").build();
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
