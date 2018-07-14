package gq.shiwenhao.naiverpc.loadbalance;

import gq.shiwenhao.naiverpc.entities.ProviderHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LoadBalanceEngine {
    private static Logger logger = LoggerFactory.getLogger(LoadBalanceEngine.class);
    private static final Map<LoadBalanceStrategyEnum, LoadBalanceStrategy>
            loadBalanceMap = new HashMap<>();

    static{
       loadBalanceMap.put(LoadBalanceStrategyEnum.Random,
               new RandomStrategy());
       loadBalanceMap.put(LoadBalanceStrategyEnum.Polling,
               new PollingStategy());
       loadBalanceMap.put(LoadBalanceStrategyEnum.WeightPolling,
               new WeightPollingStrategy());
       loadBalanceMap.put(LoadBalanceStrategyEnum.WeightRandom,
               new WeightRandomStrategy());
    }

    public static LoadBalanceStrategy queryLoadBalance(String clusterStrategy){
        LoadBalanceStrategyEnum loadBalanceStrategyEnum =
                LoadBalanceStrategyEnum.queryByCode(clusterStrategy);
        if(loadBalanceStrategyEnum == null) {
            logger.info("The load balance strategy is RANDOM");
            return loadBalanceMap.get(LoadBalanceStrategyEnum.Random);
        }

        LoadBalanceStrategy loadBalanceStrategy = loadBalanceMap.get(loadBalanceStrategyEnum);
        logger.info("The load balance strategy is " +  clusterStrategy);
        return loadBalanceStrategy;
    }

}
