package gq.shiwenhao.naiverpc.loadbalance;

import gq.shiwenhao.naiverpc.entities.ProviderHost;

import java.util.List;

public interface LoadBalanceStrategy {
    ProviderHost select(List<ProviderHost> providerHosts);
}
