package gq.shiwenhao.naiverpc.loadbalance;

public enum LoadBalanceEnum {
    Random, WeightRandom, Polling, WeightPolling;

    public static LoadBalanceEnum queryByCode(String clusterStrategy){
        if("Random".equals(clusterStrategy)){
            return LoadBalanceEnum.Random;
        } else if("WeightRandom".equals(clusterStrategy)){
            return LoadBalanceEnum.WeightRandom;
        } else if("Pooling".equals(clusterStrategy)){
            return LoadBalanceEnum.Polling;
        } else if("WeightPooling".equals(clusterStrategy)){
            return LoadBalanceEnum.WeightPolling;
        }
        return null;
    }
}
