package gq.shiwenhao.naiverpc.loadbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LoadBalanceEngine {
    private static Logger logger = LoggerFactory.getLogger(LoadBalanceEngine.class);
    private static final Map<LoadBalanceEnum, LoadBalanceStrategy>
            loadBalanceMap = new HashMap<>();

    static{
       loadBalanceMap.put(LoadBalanceEnum.Random,
               new RandomStrategy());
       loadBalanceMap.put(LoadBalanceEnum.Polling,
               new PollingStategy());
       loadBalanceMap.put(LoadBalanceEnum.WeightPolling,
               new WeightPollingStrategy());
       loadBalanceMap.put(LoadBalanceEnum.WeightRandom,
               new WeightRandomStrategy());
    }

    public static LoadBalanceStrategy queryLoadBalance(String clusterStrategy){
        LoadBalanceEnum loadBalanceStrategyEnum =
                LoadBalanceEnum.queryByCode(clusterStrategy);
        if(loadBalanceStrategyEnum == null) {
            logger.info("The load balance strategy is RANDOM");
            return loadBalanceMap.get(LoadBalanceEnum.Random);
        }

        LoadBalanceStrategy loadBalanceStrategy = loadBalanceMap.get(loadBalanceStrategyEnum);
        logger.info("The load balance strategy is " +  clusterStrategy);
        return loadBalanceStrategy;
    }

}
