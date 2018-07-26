package servicegoverntest;

import com.alibaba.fastjson.JSON;
import gq.shiwenhao.naiverpc.entities.ProviderHost;
import gq.shiwenhao.naiverpc.servicegovern.ZookeeperManager;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ManagerTest {
    private String zookeeperHost = "127.0.0.1:2181";
    private ZookeeperManager zookeeperManager = new ZookeeperManager(zookeeperHost);
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Test
    public void createNodeTest() throws InterruptedException {
        System.out.println("createNodeTest");

        ProviderHost providerHost = new ProviderHost();
        providerHost.setHost("127.0.0.1");
        providerHost.setPort(8080);
        providerHost.setWeight(1);

        zookeeperManager.createNode("/register/host1", JSON.toJSONString(providerHost).getBytes());

        byte[] bytes = zookeeperManager.getNodeInformation("/register/host1");
        assert (providerHost.equals(JSON.parseObject(bytes, ProviderHost.class)));
        return;
        //countDownLatch.await(); 方便手动查看Zookeeper
    }

    @Test
    public void serviceDiscoveryTest() throws InterruptedException {
        System.out.println("serviceDiscoveryTest");

        ProviderHost providerHost = new ProviderHost();
        providerHost.setHost("127.0.0.1");
        providerHost.setPort(8080);
        providerHost.setWeight(1);

        zookeeperManager.createNode("/service/host1", JSON.toJSONString(providerHost).getBytes());
        zookeeperManager.createNode("/service/host2", JSON.toJSONString(providerHost).getBytes());

        List<String> providers = zookeeperManager.getChildrenNode("/service");
        assert (providers.size() == 2);

        //countDownLatch.await();
    }

    @Test
    public void listTest(){
        System.out.println("listTest");

        ProviderHost providerHost = new ProviderHost();
        ProviderHost providerHostFake = new ProviderHost();
        providerHost.setHost("127.0.0.1");
        providerHostFake.setHost("127.0.0.1");
        providerHost.setPort(9000);
        providerHostFake.setPort(9000);
        providerHost.setWeight(1);
        providerHostFake.setWeight(2);

        List<ProviderHost> providerHosts = new LinkedList<>();
        providerHosts.add(providerHost);
        providerHosts.remove(providerHostFake);
        providerHosts.add(providerHostFake);

        for(ProviderHost p : providerHosts){
            System.out.println(p);
        }
    }
}
