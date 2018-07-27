package demo.api;

import demo.SayHello;
import demo.SayHelloImpl;
import gq.shiwenhao.naiverpc.endpoint.ProviderPublisher;

class Server {
    private ProviderPublisher providerPublisher;

    public Server(){
        providerPublisher = new ProviderPublisher.Builder("127.0.0.1:2181", 9000,
                SayHello.class, new SayHelloImpl()).build();

    }

}
