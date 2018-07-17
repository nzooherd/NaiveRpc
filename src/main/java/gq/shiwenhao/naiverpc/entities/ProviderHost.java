package gq.shiwenhao.naiverpc.entities;

public class ProviderHost {
    private String host;
    private int port;
    private int weight = 1;

    public ProviderHost(){

    }
    public ProviderHost(String host, int port){
        this.host = host;
        this.port = port;
    }
    public ProviderHost(String host, int port, int weight){
       this.host = host;
       this.port = port;
       this.weight = weight;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public int hashCode(){
        return host.hashCode() | port;
    }

    @Override
    public boolean equals(Object o){
        ProviderHost providerHostTemp = (ProviderHost)o;
        return providerHostTemp.port == port &&
                host.equals(providerHostTemp.host);
    }

    @Override
    public String toString(){
        return "host: " + host + " port: " + port + " weight: " + weight;
    }

}
