package demo.api;

import gq.shiwenhao.naiverpc.endpoint.ProviderPublisher;

import java.util.Arrays;

class QuickSortImp implements QuickSort{

    @Override
    public int[] sort(int[] numbers) {
        Arrays.sort(numbers);
        return numbers;
    }
}
class SayHelloImpl implements SayHello{

    @Override
    public String sayHello(String word) {
        return "Hello " + word;
    }
}

public class Server {
    private ProviderPublisher providerPublisher;

    public Server(){
        providerPublisher = new ProviderPublisher.Builder("127.0.0.1:2181", 9000,
                SayHello.class, new SayHelloImpl()).build();

    }

    public static void main(String[] args){
        Server server = new Server();

    }

}
