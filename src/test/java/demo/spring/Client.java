package demo.spring;

import gq.shiwenhao.naiverpc.endpoint.ProxyFactory;
import gq.shiwenhao.naiverpc.transport.RpcFuture;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Client {
    private BeanFactory beanFactory;

    public Client() {
        beanFactory = new ClassPathXmlApplicationContext("client-proxy.xml");
    }
    public String callASync() {
        ProxyFactory proxyFactory = (ProxyFactory) beanFactory.getBean("springConsumerProxy");
        RpcFuture rpcFuture = proxyFactory.call("sayHello", new Object[]{"World"});
        return (String)rpcFuture.get();
    }
}
