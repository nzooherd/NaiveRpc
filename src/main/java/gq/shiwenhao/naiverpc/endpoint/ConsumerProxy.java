package gq.shiwenhao.naiverpc.endpoint;
import gq.shiwenhao.naiverpc.loadbalance.LoadBalanceEnum;
import gq.shiwenhao.naiverpc.servicegovern.ServiceDiscover;
import gq.shiwenhao.naiverpc.servicegovern.ZookeeperManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;

public class ConsumerProxy {
    private static Logger logger = LoggerFactory.getLogger(ConsumerProxy.class);

    private String zookeeperHost;
    private Class<?> interfaceClass;

    private boolean retryRequest;
    private int retryTimes;
    private int timeout;
    private LoadBalanceEnum loadBalanceEnum;

    private ZookeeperManager zookeeperManager;
    private ServiceDiscover serviceDiscovery;

    public ConsumerProxy(Builder builder){
        this.zookeeperHost = builder.zookeeperHost;
        this.interfaceClass = builder.interfaceClass;

        this.retryRequest = builder.retryRequest;
        this.retryTimes = builder.retryTimes;
        this.timeout = builder.timeout;
        this.loadBalanceEnum = builder.loadBalanceEnum;

        zookeeperManager = new ZookeeperManager(zookeeperHost);
        serviceDiscovery = new ServiceDiscover(interfaceClass, zookeeperManager, loadBalanceEnum);
    }


    public static class Builder{
        private static Logger logger = LoggerFactory.getLogger(Builder.class);

        //Required parameters
        private String zookeeperHost;
        private Class interfaceClass;

        //Optional parameters
        private LoadBalanceEnum loadBalanceEnum = LoadBalanceEnum.Random;
        private boolean retryRequest = true;
        private int retryTimes = 3;
        private int timeout = 2000;  //单位ms

        public Builder(String zookeeperHost, Class interfaceClass){
            this.zookeeperHost = zookeeperHost;
            this.interfaceClass = interfaceClass;
        }
        public Builder(String zookeeperHost, String interfaceName){
            this.zookeeperHost = zookeeperHost;
            try{
                this.interfaceClass = Class.forName(interfaceName);
            } catch (ClassNotFoundException e){
                logger.error("Can't found class:" + interfaceName + " error:" + e.getMessage());
            }
        }

        public Builder loadBalanceEnum(LoadBalanceEnum loadBalanceEnum){
            this.loadBalanceEnum = loadBalanceEnum;
            return this;
        }
        public Builder retryRequest(boolean retryRequest){
            this.retryRequest = retryRequest;
            return this;
        }
        public Builder retryTimes(int retryTimes){
            this.retryTimes = retryTimes;
            return this;
        }
        public Builder timeout(int timeout){
            this.timeout = timeout;
            return this;
        }

        public ConsumerProxy build(){
            return new ConsumerProxy(this);
        }

    }

    public Object callSync(){
        ProxyFactory proxyFactory = new ProxyFactory(interfaceClass, serviceDiscovery);
        if(retryRequest) {
            proxyFactory.setTimeControl(retryRequest, retryTimes, timeout);
        }

        return Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class[]{interfaceClass}, proxyFactory);
    }

    public ProxyFactory callAsync(){
        ProxyFactory proxyFactory = new ProxyFactory(interfaceClass, serviceDiscovery);
        if(retryRequest) {
            proxyFactory.setTimeControl(retryRequest, retryTimes, timeout);
        }
        return proxyFactory;
    }

}
