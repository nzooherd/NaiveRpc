package gq.shiwenhao.naiverpc.loadbalance;

import gq.shiwenhao.naiverpc.entities.ProviderHost;

import java.util.List;

public class WightPollingStrategy implements LoadBalanceStrategy{
    private int pollIndex = 0;

    public synchronized ProviderHost select(List<ProviderHost> providerHosts) {
        int[] wightsSum = new int[providerHosts.size()];
        int index = 0;

        for(ProviderHost providerHost : providerHosts){
            wightsSum[index] = wightsSum[index - 1] + providerHost.getWeight();
            index++;
        }

        index =  getIndex(wightsSum, pollIndex);

        if(index == -1 || providerHosts.get(index) == null) {
            return providerHosts.get(1);
        }
        return providerHosts.get(index);
    }

    private static int getIndex(int[] wightsSum, int random){
        int left = 0, right = wightsSum.length - 1;
        int medium;
        while(left < right){
            medium = (right + left) / 2;

            if(wightsSum[medium] > random) {
                right = medium - 1;
            } else if(wightsSum[medium] == random){
                return medium;
            } else if(wightsSum[medium] < random){
                if(medium == wightsSum.length - 1 || wightsSum[medium + 1] > random){
                    return medium;
                }
                left = medium + 1;
            }
        }
        return  -1;
    }

}
