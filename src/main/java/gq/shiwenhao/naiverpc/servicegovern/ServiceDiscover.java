package gq.shiwenhao.naiverpc.servicegovern;

import com.alibaba.fastjson.JSON;
import gq.shiwenhao.naiverpc.entities.ProviderHost;
import gq.shiwenhao.naiverpc.loadbalance.LoadBalanceEngine;
import gq.shiwenhao.naiverpc.loadbalance.LoadBalanceEnum;
import gq.shiwenhao.naiverpc.loadbalance.LoadBalanceStrategy;
import gq.shiwenhao.naiverpc.transport.ConnectManager;
import gq.shiwenhao.naiverpc.utils.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

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

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public ServiceDiscover(Class interfaceClass, ZookeeperManager zookeeperManager, LoadBalanceEnum loadBalanceEnum){
        this.interfaceClass = interfaceClass;
        this.zookeeperManager = zookeeperManager;
        this.loadBalanceStrategy = LoadBalanceEngine.queryLoadBalance(loadBalanceEnum);

        host = NetUtil.getHost();
        if(host == null){
            logger.error("Get local host failure");
        }
        consumerPath = "/" + interfaceClass.getName() + "/consumers/" + host;
        providersPath = "/" + interfaceClass.getName() + "/providers";

        init();
    }

    private void init() {
        connectManager = ConnectManager.getInstance(this);

        zookeeperManager.createNode(consumerPath);
        logger.info("Start get providers host");
        List<String> providers = zookeeperManager.getChildrenNode(providersPath);
        for(String provider : providers){
            String path = providersPath + "/" + provider;
            ProviderHost providerHost = JSON.parseObject(new String(zookeeperManager.getNodeInformation(path)),
                    ProviderHost.class);
            connectManager.connectServerNode( providerHost);
        }
        logger.info("End get providers host");
        zookeeperManager.watchNode(providersPath, providersListener);
    }

    public void tryAddConnectNode(ProviderHost providerHost){
        connectManager.addServerNode(providerHost);
    }
    public void addConnectNode(ProviderHost providerHost){
        providerHosts.add(providerHost);
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

    public void countDown(){
        if(countDownLatch.getCount() == 1) {
            countDownLatch.countDown();
        }
    }

    public ProviderHost serviceLoadBalance(){
        if(countDownLatch.getCount() == 1) {
            logger.warn("The connection list is null. Is waiting connect to server!");
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("Service discovery wait countdown throw exception:{}.", e.getMessage());
        }
        return  loadBalanceStrategy.select(providerHosts);
    }

}
