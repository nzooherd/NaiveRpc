package gq.shiwenhao.naiverpc.servicegovern;

import com.alibaba.fastjson.JSON;
import gq.shiwenhao.naiverpc.entities.ProviderHost;
import gq.shiwenhao.naiverpc.utils.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceRegister {
    private static Logger logger = LoggerFactory.getLogger(ServiceRegister.class);

    private ZookeeperManager zookeeperManager;
    private ProviderHost providerHost;

    private String providerPath;
    private String host;

    public ServiceRegister(ZookeeperManager zookeeperManager, Class interfaceClass,
                           int port, int weight){
        this.zookeeperManager = zookeeperManager;

        host = NetUtil.getHost();
        if(host == null){
            logger.error("Get local host failure");
        }

        providerPath = "/" + interfaceClass.getName() + "/providers/" + host + ":" + port;
        providerHost = new ProviderHost(host, port, weight);

    }

    public void serviceRegister(){
        //采用Json序列化，方便阅读手动调试
        zookeeperManager.createNode(providerPath, JSON.toJSONString(providerHost).getBytes());
    }
}
