package demo.spring;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Server {
    public Server(){
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("provider-publisher.xml");
    }
}
