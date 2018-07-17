package zookeepertest;

import com.alibaba.fastjson.JSON;
import gq.shiwenhao.naiverpc.entities.ProviderHost;
import gq.shiwenhao.naiverpc.servicegovern.ZookeeperManager;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ManagerTest {
    private String zookeeperHost = "127.0.0.1:2181";
    private ZookeeperManager zookeeperManager = new ZookeeperManager(zookeeperHost);
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Test
    public void createNodeTest() throws InterruptedException {
        ProviderHost providerHost = new ProviderHost();
        providerHost.setHost("127.0.0.1");
        providerHost.setPort(8080);
        providerHost.setWeight(1);

        zookeeperManager.createNode("/register/host1", JSON.toJSONString(providerHost).getBytes());

        byte[] bytes = zookeeperManager.getNodeInformation("/register/host1");
        assert (providerHost.equals(JSON.parseObject(bytes, ProviderHost.class)));

        //countDownLatch.await(); 方便手动查看Zookeeper
    }
}
