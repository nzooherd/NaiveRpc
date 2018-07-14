package gq.shiwenhao.naiverpc.servicegovern;

import com.alibaba.fastjson.JSON;
import gq.shiwenhao.naiverpc.entities.ProviderHost;
import gq.shiwenhao.naiverpc.loadbalance.LoadBalanceEngine;
import gq.shiwenhao.naiverpc.loadbalance.LoadBalanceStrategy;
import gq.shiwenhao.naiverpc.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceDiscover {
    private static Logger logger = LoggerFactory.getLogger(ServiceDiscover.class);

    private List<ProviderHost> providerHosts = new CopyOnWriteArrayList<>();

    private ZookeeperManager zookeeperManager;
    private LoadBalanceStrategy loadBalanceStrategy;
    private ProvidersListener providersListener = new ProvidersListener(providerHosts);

    private Class interfaceClass;
    private String consumerPath;
    private String providersPath;
    private String host;


    public ServiceDiscover(Class interfaceClass, ZookeeperManager zookeeperManager){
        this.interfaceClass = interfaceClass;
        this.zookeeperManager = zookeeperManager;

        host = NetUtils.getHost();
        if(host == null){
            logger.error("Get local host failure");
        }
        consumerPath = "/register/" + interfaceClass.getName() + "/consumers/" + host;
        providersPath = "/register/" + interfaceClass.getName() + "/providers";

        init();
    }


    private void init() {
        zookeeperManager.createNode(consumerPath);
        logger.info("Start get providers host");
        List<String> providers = zookeeperManager.getChildrenNode(providersPath);
        for(String provider : providers){
            String path = providersPath + "/" + provider;
            providerHosts.add(JSON.parseObject(
                    new String(zookeeperManager.getNodeInformation(path)), ProviderHost.class));
        }
        logger.info("End get providers host");
        zookeeperManager.watchNode(providersPath, providersListener);
    }

    public void setLoadBalanceStrategy(String clusterStrategy){
        this.loadBalanceStrategy = LoadBalanceEngine.queryLoadBalance(clusterStrategy);
    }

    public ProviderHost serviceLoadBalance(){
        return loadBalanceStrategy.select(providerHosts);
    }

}
