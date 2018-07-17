package gq.shiwenhao.naiverpc.servicegovern;

import com.alibaba.fastjson.JSON;
import gq.shiwenhao.naiverpc.entities.ProviderHost;
import gq.shiwenhao.naiverpc.loadbalance.LoadBalanceEngine;
import gq.shiwenhao.naiverpc.loadbalance.LoadBalanceStrategy;
import gq.shiwenhao.naiverpc.transport.ConnectManager;
import gq.shiwenhao.naiverpc.utils.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceDiscover {
    private static Logger logger = LoggerFactory.getLogger(ServiceDiscover.class);

    private ConnectManager connectManager;

    private ZookeeperManager zookeeperManager;
    private LoadBalanceStrategy loadBalanceStrategy;
    private ProvidersListener providersListener = new ProvidersListener(this);
    private List<ProviderHost> providerHosts = new CopyOnWriteArrayList<>();

    private Class interfaceClass;
    private String consumerPath;
    private String providersPath;
    private String host;


    public ServiceDiscover(Class interfaceClass, ZookeeperManager zookeeperManager){
        this.interfaceClass = interfaceClass;
        this.zookeeperManager = zookeeperManager;

        host = NetUtil.getHost();
        if(host == null){
            logger.error("Get local host failure");
        }
        consumerPath = interfaceClass.getName() + "/consumers/" + host;
        providersPath = interfaceClass.getName() + "/providers";

        init();
    }


    private void init() {
        connectManager = ConnectManager.getInstance();

        zookeeperManager.createNode(consumerPath);
        logger.info("Start get providers host");
        List<String> providers = zookeeperManager.getChildrenNode(providersPath);
        for(String provider : providers){
            String path = providersPath + "/" + provider;
            providerHosts.add(JSON.parseObject(new String(zookeeperManager.getNodeInformation(path)),
                    ProviderHost.class));
        }
        logger.info("End get providers host");
        zookeeperManager.watchNode(providersPath, providersListener);
    }

    public void addConnectNode(ProviderHost providerHost){
        providerHosts.add(providerHost);
        connectManager.addServerNode(providerHost);
    }
    public void deleteConnectedNode(ProviderHost providerHost){
        providerHosts.remove(providerHost);
        connectManager.removeServerNode(providerHost);

    }
    public void updateConnectedNode(ProviderHost providerHost){
        providerHosts.remove(providerHost);
        providerHosts.add(providerHost);
        connectManager.updateServerNode(providerHost);
    }

    public void setLoadBalanceStrategy(String clusterStrategy){
        this.loadBalanceStrategy = LoadBalanceEngine.queryLoadBalance(clusterStrategy);
    }

    public ProviderHost serviceLoadBalance(){
       return  loadBalanceStrategy.select(providerHosts);
    }

}
