package gq.shiwenhao.naiverpc.loadbalance;

import gq.shiwenhao.naiverpc.entities.ProviderHost;

import java.util.List;

public class LoadBalance {
    private List<ProviderHost> providerHosts;

    public LoadBalance(List<ProviderHost> providerHosts){
        this.providerHosts = providerHosts;
    }


}
