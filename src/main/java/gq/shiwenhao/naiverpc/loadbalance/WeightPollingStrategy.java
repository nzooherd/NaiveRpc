package gq.shiwenhao.naiverpc.loadbalance;

import gq.shiwenhao.naiverpc.entities.ProviderHost;
import gq.shiwenhao.naiverpc.utils.LoadBalanceUtil;

import java.util.List;

public class WeightPollingStrategy implements LoadBalanceStrategy{
    private int pollIndex = 0;

    public ProviderHost select(List<ProviderHost> providerHosts) {
        int[] wightsSum = new int[providerHosts.size()];
        int index = 0;

        for(ProviderHost providerHost : providerHosts){
            wightsSum[index] = wightsSum[index - 1] + providerHost.getWeight();
            index++;
        }

        index = LoadBalanceUtil.getIndex(wightsSum, pollIndex);

        if(index == -1 || providerHosts.get(index) == null) {
            return providerHosts.get(1);
        }
        return providerHosts.get(index);
    }


}
