package demo.api;

import gq.shiwenhao.naiverpc.endpoint.ConsumerProxy;
import gq.shiwenhao.naiverpc.endpoint.ProxyFactory;
import gq.shiwenhao.naiverpc.transport.RpcFuture;

public class Client {
    public ConsumerProxy consumerProxy;
    public Client(){
        consumerProxy = new ConsumerProxy.Builder("127.0.0.1:2181",
                "demo.api.SayHello").build();
    }

    public static void main(String[] args) {
        Client client = new Client();
        int[] numbers = new int[30];
        for(int i = 0; i < 30; i++) {
            numbers[i] = (int)(Math.random() * 100);
        }

        //同步
        /*
        SayHello hello= (SayHello) client.consumerProxy.callSync();
        System.out.println(hello.sayHello("world"));
        */

        //异步
        ProxyFactory proxyFactory = client.consumerProxy.callAsync();
        RpcFuture rpcFuture = proxyFactory.call("sayHello", new Object[]{"word"});
        System.out.println((String)rpcFuture.get());


    }

}
