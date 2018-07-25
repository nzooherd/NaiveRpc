package gq.shiwenhao.naiverpc.endpoint;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class SpringProviderPublisher implements FactoryBean<Object>, InitializingBean {

    private Class interfaceClass;
    private String interfaceClassName;

    private Object interfaceImpl;
    private String zookeeperHost;
    private int port;

    private int weight = 1;
    private int maxRequestMessageLength = 65535;

    public void setInterfaceClassName(String interfaceClassName) throws ClassNotFoundException {
        this.interfaceClassName = interfaceClassName;
        this.interfaceClass = Class.forName(interfaceClassName);
    }

    public void setInterfaceImpl(Object interfaceImpl) {
        this.interfaceImpl = interfaceImpl;
    }

    public void setZookeeperHost(String zookeeperHost) {
        this.zookeeperHost = zookeeperHost;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setMaxRequestMessageLength(int maxRequestMessageLength) {
        this.maxRequestMessageLength = maxRequestMessageLength;
    }

    private ProviderPublisher providerPublisher;

    @Override
    public Object getObject() throws Exception {
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return this.getClass();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        providerPublisher = new ProviderPublisher.Builder(zookeeperHost, port, interfaceClass, interfaceImpl)
                .weight(weight)
                .maxRequestMessageLength(maxRequestMessageLength)
                .build();
    }
}
