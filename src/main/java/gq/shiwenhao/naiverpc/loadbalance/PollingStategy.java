package gq.shiwenhao.naiverpc.loadbalance;

import gq.shiwenhao.naiverpc.entities.ProviderHost;

import java.util.List;

public class PollingStategy implements LoadBalanceStrategy{
    private int pollIndex = 0;

    public synchronized ProviderHost select(List<ProviderHost> providerHosts) {
        ProviderHost service =  providerHosts.get(pollIndex);
        pollIndex++;
        if(pollIndex > providerHosts.size()) {
            pollIndex = 0;
        }

        if(service == null){
            service = providerHosts.get(0);
        }
        return service;
    }
}
