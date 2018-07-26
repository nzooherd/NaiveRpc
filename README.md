# NaiveRpc
*NaiveRpc is a project base on java language development. It is an Rpc(Remote Procedure Call) framework based on Netty, Zookeeper and Spring. Because of the poor level of myself, I call it naiverpc.*

**[Read more](http://shiwenhao.gq/2018/07/10/%E8%B0%88%E8%B0%88RPC/#more)**

Author|Flying Cat
------|---------
Email|cat@shiwenhao.gq

## Features
* Transport in netty
* Serialization in kryo
* Service register center is ZooKeeper
* Support BIO and NO-BIO(similar Class Future<T\> in Java) call
* Spring integration

## Design:
![](https://camo.githubusercontent.com/04f697d846d579a5472cf63fb8b78e226ae58a5c/68747470733a2f2f696d61676573323031352e636e626c6f67732e636f6d2f626c6f672f3433343130312f3230313630332f3433343130312d32303136303331363130323635313633312d313831363036343130352e706e67)

## Quick Start
### Maven dependency
```
<dependency>
    <groupId>gq.shiwenhao</groupId>
    <artifactId>naive-rpc</artifactId>
    <version>1.0.0</version>
</dependency>
```
### Demo
NaiveRpc supports use in api and spring.

Assume there is an interface
```
package demo.rpc;
public interface SayHello(){
    String sayHelloToOne(String one);
}
```
And next is an implement class.
```
public class SayHelloImpl implements SayHello{
    @Override
    public String sayHelloToOne(String one){
        return "Hello!" + one;
    }
}
```
We can use it like this.
#### API
```
/*
 * Start Server
 */
ProviderPublisher provider = new ProviderPublisher.Builder("127.0.0.1:2181", 9000,
                SayHello.class, new SayHelloImpl()).build();

/*
 * Start Client
 */
ConsumerProxy consumser = new ConsumerProxy.Builder("127.0.0.1:2181",
                "demo.rpc.SayHello").build();

/*
 * Sync
 */
SayHello sayHello = consumer.callSync();
System.out.println(sayHello.sayHelloToOne("Li Hua"));

/*
 * Async
 */
ProxyFactory proxyFactory = consumerProxy.callAsync();
RpcFuture rpcFuture = proxyFactory.call("sayHelloToOne", new Object[]{"world"});
//Somethind need to do
System.out.println(rpcFuture.get());
```
#### Spring
```
```

### Parameter
* Client

Property|Parameter|comment
-------|------|-------
must|ZookeeperHost(String)|Zookeeper server address
must|InterfaceClass(String/Class)|Interface class
optional|LoadBalanceEnum|LoadBalanceEnum.Random,WeightRandom,Polling,WeightPolling，Random Deafult
optional|RetryRequest(Boolean)|
optional|RetryTimes(int)|
optional|Timeout(int /ms)|

* Server

Property|Parameter|comment
-------|------|-------
must|ZookeeperHost(String)|Zookeeper server address
must|port(int)|Listen port
must|InterfaceClass(String/Class)|Interface class
must|InterfaceImpl(Object)|Impl Object
optional|weight(int)|Server load balance weight, Default 1
optional|MaxRequestMessageLength(int)|Default 65535, default is ok 

## TODO
* Optimize parameters set method of Spring
* Mock test
* Add hash load balance stargegy
* Others

## License
Apache License, Version 2.0
