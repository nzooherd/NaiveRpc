package gq.shiwenhao.naiverpc.endpoint;
import gq.shiwenhao.naiverpc.loadbalance.LoadBalanceStrategyEnum;
import gq.shiwenhao.naiverpc.servicegovern.ServiceDiscover;
import gq.shiwenhao.naiverpc.servicegovern.ZookeeperManager;

public class ConsumerProxy {
    private String zookeeperHost;
    private Class interfaceClass;

    private String loadBalanceStrategy;

    private ZookeeperManager zookeeperManager;
    private ServiceDiscover serviceDiscovery;

    public ConsumerProxy(Class interfaceClass, String zookeeperHost){
        this.zookeeperHost = zookeeperHost;
        this.interfaceClass = interfaceClass;
        zookeeperManager = new ZookeeperManager(zookeeperHost);
        serviceDiscovery = new ServiceDiscover(interfaceClass, zookeeperManager);
        serviceDiscovery.setLoadBalanceStrategy(loadBalanceStrategy);
    }

    public Object getProxy(){
        return null;
    }

}
