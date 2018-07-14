package gq.shiwenhao.naiverpc.loadbalance;

public enum LoadBalanceStrategyEnum {
    Random, WeightRandom, Polling, WeightPolling;

    public static LoadBalanceStrategyEnum queryByCode(String clusterStrategy){
        if("Random".equals(clusterStrategy)){
            return LoadBalanceStrategyEnum.Random;
        } else if("WeightRandom".equals(clusterStrategy)){
            return LoadBalanceStrategyEnum.WeightRandom;
        } else if("Pooling".equals(clusterStrategy)){
            return LoadBalanceStrategyEnum.Polling;
        } else if("WeightPooling".equals(clusterStrategy)){
            return LoadBalanceStrategyEnum.WeightPolling;
        }
        return null;
    }
}
