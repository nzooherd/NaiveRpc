package ZookeeperTest;

import gq.shiwenhao.naiverpc.servicegovern.ZookeeperManager;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ManagerTest {
    private String zookeeperHost = "127.0.0.1:2181";
    private ZookeeperManager zookeeperManager = new ZookeeperManager(zookeeperHost);


}
