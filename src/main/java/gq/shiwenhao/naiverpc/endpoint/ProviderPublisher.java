package gq.shiwenhao.naiverpc.endpoint;

import gq.shiwenhao.naiverpc.servicegovern.ServiceRegister;
import gq.shiwenhao.naiverpc.servicegovern.ZookeeperManager;

public class ProviderPublisher {
    private Class interfaceClass;
    private Object interfaceImpl;
    private String zookeeperHost;
    private int port;

    private ZookeeperManager zookeeperManager;
    private ServiceRegister serviceRegister;

    public ProviderPublisher(Class interfaceClass, Object interfaceImpl, String zookeeperHost, int port){
        this.interfaceClass = interfaceClass;
        this.interfaceImpl = interfaceImpl;
        this.zookeeperHost = zookeeperHost;
        this.port = port;

        zookeeperManager = new ZookeeperManager(zookeeperHost);
        serviceRegister = new ServiceRegister(zookeeperManager, interfaceClass, port);
    }

}
