package demo.api;

public class SayHelloImpl implements SayHello{

    @Override
    public String sayHello(String word) {
        return "Hello " + word;
    }
}