package gq.shiwenhao.naiverpc.endpoint;

import gq.shiwenhao.naiverpc.loadbalance.LoadBalanceEnum;
import gq.shiwenhao.naiverpc.servicegovern.ServiceDiscover;
import gq.shiwenhao.naiverpc.servicegovern.ZookeeperManager;
import gq.shiwenhao.naiverpc.transport.RpcFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class SpringConsumerProxy implements FactoryBean<Object>, InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(SpringConsumerProxy.class);

    private String zookeeperHost;
    private Class<?> interfaceClass;
    private String interfaceClassName;

    private boolean async = false;
    private boolean retryRequest = true;
    private int retryTimes = 3;
    private int timeout = 2000;
    private LoadBalanceEnum loadBalanceEnum = LoadBalanceEnum.Random;

    public void setZookeeperHost(String zookeeperHost) {
        this.zookeeperHost = zookeeperHost;
    }

    public void setInterfaceClassName(String interfaceClassName) throws ClassNotFoundException {
        this.interfaceClassName = interfaceClassName;
        this.interfaceClass = Class.forName(interfaceClassName);
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public void setRetryRequest(boolean retryRequest) {
        this.retryRequest = retryRequest;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setLoadBalanceEnum(LoadBalanceEnum loadBalanceEnum) {
        this.loadBalanceEnum = loadBalanceEnum;
    }

    private ZookeeperManager zookeeperManager;
    private ServiceDiscover serviceDiscovery;

    private Object proxy;
    private ProxyFactory proxyFactory;

    private ConsumerProxy consumerProxy;

    @Override
    public Object getObject() throws Exception {
        return async ? proxyFactory : proxy;
    }

    @Override
    public boolean isSingleton(){
        return true;
    }

    @Override
    public Class<?> getObjectType() {
        return async ? RpcFuture.class : interfaceClass;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        consumerProxy = new ConsumerProxy.Builder(zookeeperHost, interfaceClass)
                .retryRequest(retryRequest)
                .loadBalanceEnum(loadBalanceEnum)
                .retryTimes(retryTimes)
                .timeout(timeout)
                .build();
        proxy = consumerProxy.callSync();
        proxyFactory = consumerProxy.callAsync();
    }
}
