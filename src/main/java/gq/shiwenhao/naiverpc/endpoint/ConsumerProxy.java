package gq.shiwenhao.naiverpc.endpoint;
import gq.shiwenhao.naiverpc.servicegovern.ServiceDiscover;
import gq.shiwenhao.naiverpc.servicegovern.ZookeeperManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerProxy {
    private static Logger logger = LoggerFactory.getLogger(ConsumerProxy.class);

    private String zookeeperHost;
    private Class<?> interfaceClass;

    private String loadBalanceStrategy;

    private ZookeeperManager zookeeperManager;
    private ServiceDiscover serviceDiscovery;

    public ConsumerProxy(String zookeeperHost){
        this.zookeeperHost = zookeeperHost;
        zookeeperManager = new ZookeeperManager(zookeeperHost);
        serviceDiscovery.setLoadBalanceStrategy(loadBalanceStrategy);
    }
    public ConsumerProxy(String className, String zookeeperHost){
        this(zookeeperHost);

        try {
            this.interfaceClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.error("Class not fount " + e.getMessage());
        }
    }


    public <T> T getProxySync(){
        return null;
    }
    public <T> T getProxySync(Class<T> interfaceClass){
        return null;
    }


}
