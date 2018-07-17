package gq.shiwenhao.naiverpc.servicegovern;

import com.alibaba.fastjson.JSON;
import gq.shiwenhao.naiverpc.entities.ProviderHost;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProvidersListener implements PathChildrenCacheListener {
    private static Logger logger = LoggerFactory.getLogger(ProvidersListener.class);

    private ServiceDiscover serviceDiscover;

    public ProvidersListener(ServiceDiscover serviceDiscover){
        this.serviceDiscover = serviceDiscover;
    }

    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) {
        switch (event.getType()){
            case CHILD_ADDED:
                logger.info("Provider:" + event.getData().getPath() + " was added");
                ProviderHost addProviderHost =
                        JSON.parseObject(event.getData().getData(), ProviderHost.class);
                serviceDiscover.addConnectNode(addProviderHost);
                break;
            case CHILD_REMOVED:
                logger.info("Provider:" + event.getData().getPath() + " was removed");
                ProviderHost removeProviderHost =
                        JSON.parseObject(event.getData().getData(), ProviderHost.class);
                serviceDiscover.deleteConnectedNode(removeProviderHost);
                break;
            case CHILD_UPDATED:
                logger.info("Provider:" + event.getData().getPath() + " was updated");
                ProviderHost updateProviderHost =
                        JSON.parseObject(event.getData().getData(), ProviderHost.class);
                serviceDiscover.updateConnectedNode(updateProviderHost);
                break;
            default:
                break;
        }
    }
}
