package gq.shiwenhao.naiverpc.servicegovern;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;


public class ZookeeperManager {
    private static Logger logger = LoggerFactory.getLogger(ZookeeperManager.class);

    private final String NAMESPACE = "register";
    private String zookeeperHost;
    private CuratorFramework client;
    private RetryPolicy retryPolicy;

    public ZookeeperManager(String zookeeperHost){
        this.zookeeperHost = zookeeperHost;

        retryPolicy = new ExponentialBackoffRetry(1000, 3);
        logger.info("Start to connect zookeeper");
        client = CuratorFrameworkFactory.builder()
                .connectString(zookeeperHost)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(retryPolicy)
                .namespace(NAMESPACE)
                .build();
        client.start();
        logger.info("zookeeper://" + zookeeperHost + " connect success");
    }

    public void createNode(String path) {
        try {
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(path);
        } catch (Exception e) {
            logger.error("Create node:" + path + " error:" + e.getMessage());
        }
    }
    public void createNode(String path, byte[] information) {
        try {
            client.create()
                   .creatingParentsIfNeeded()
                   .withMode(CreateMode.EPHEMERAL)
                   .forPath(path, information);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Create node:" + path + " with information:"
                    + new String(information) + " failure:" + e.getMessage());
        }
    }

    public List<String> getChildrenNode(String path) {
        try {
            return client.getChildren().forPath(path);
        } catch (Exception e) {
            logger.error("Get node:" + path + " children error:" + e.getMessage());
        }
        return new LinkedList<String>();
    }
    public byte[] getNodeInformation(String path) {
        try {
            return client.getData().forPath(path);
        } catch (Exception e) {
            logger.error("Get node:" + path + " information error:" + e.getMessage());
        }
        return null;
    }

    public void watchNode(String path, PathChildrenCacheListener cacheListener){
        PathChildrenCache cache = new PathChildrenCache(client,  path, true);
        cache.getListenable().addListener(cacheListener);
        try {
            cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        } catch (Exception e) {
            logger.info("Zookeeper watch path:" + path + " throws exception:" + e.getMessage());
        }
        logger.info("Zookeeper watch path:" + path + " success!");
    }

}
