package gq.shiwenhao.naiverpc.loadbalance;

import gq.shiwenhao.naiverpc.entities.ProviderHost;
import gq.shiwenhao.naiverpc.utils.LoadBalanceUtils;

import java.util.List;

public class WeightRandomStrategy implements LoadBalanceStrategy{
    public ProviderHost select(List<ProviderHost> providerHosts) {
        int[] wightsSum = new int[providerHosts.size()];
        int index = 1;
        int MAX_LEN;
        wightsSum[0] = 0;

        for(ProviderHost providerHost : providerHosts){
            wightsSum[index] = wightsSum[index - 1] + providerHost.getWeight();
            index++;
        }
        MAX_LEN = wightsSum[index - 1] + providerHosts.get(providerHosts.size() - 1).getWeight();

        int random = (int)(Math.random() * MAX_LEN);
        index = LoadBalanceUtils.getIndex(wightsSum, random);

        if(index == -1 || providerHosts.get(index) == null) {
            return providerHosts.get(1);
        }
        return providerHosts.get(index);
    }

}
