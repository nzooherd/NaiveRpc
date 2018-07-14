package gq.shiwenhao.naiverpc.loadbalance;

import gq.shiwenhao.naiverpc.entities.ProviderHost;

import java.util.List;

public class RandomStrategy implements LoadBalanceStrategy{
    public ProviderHost select(List<ProviderHost> providerHosts) {
        int MAX_LEN = providerHosts.size();
        int index = (int)(Math.random() * MAX_LEN);
        return providerHosts.get(index);
    }
}
