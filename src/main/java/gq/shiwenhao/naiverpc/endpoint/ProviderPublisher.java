package gq.shiwenhao.naiverpc.endpoint;

import gq.shiwenhao.naiverpc.entities.RpcRequest;
import gq.shiwenhao.naiverpc.entities.RpcResponse;
import gq.shiwenhao.naiverpc.servicegovern.ServiceRegister;
import gq.shiwenhao.naiverpc.servicegovern.ZookeeperManager;
import gq.shiwenhao.naiverpc.transport.RpcDecoder;
import gq.shiwenhao.naiverpc.transport.RpcEncoder;
import gq.shiwenhao.naiverpc.transport.RpcResponseHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProviderPublisher {
    private static Logger logger = LoggerFactory.getLogger(ProviderPublisher.class);

    private Class interfaceClass;
    public Object interfaceImpl;

    private String zookeeperHost;
    private int port;
    private int weight;
    private int maxRequestMessageLength;


    private ZookeeperManager zookeeperManager;
    private ServiceRegister serviceRegister;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    public ProviderPublisher(Builder builder){
        this.interfaceClass = builder.interfaceClass;
        this.interfaceImpl = builder.interfaceImpl;
        this.zookeeperHost = builder.zookeeperHost;
        this.port = builder.port;
        this.weight = builder.weight;
        this.maxRequestMessageLength = builder.maxRequestMessageLength;

        this.zookeeperManager = new ZookeeperManager(zookeeperHost);
        this.serviceRegister = new ServiceRegister(zookeeperManager, interfaceClass, port, weight);

        try {
            logger.info("Server is launching");
            start();
        } catch (InterruptedException e) {
            logger.error("Server start error:" + e.getMessage());
        }

        logger.info("Server start success");
    }

    private void start() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(maxRequestMessageLength,
                                0, 4, 0, 4));
                        ch.pipeline().addLast(new RpcEncoder(RpcResponse.class));
                        ch.pipeline().addLast(new RpcDecoder(RpcRequest.class));
                        ch.pipeline().addLast(new RpcResponseHandler(interfaceClass, interfaceImpl));
                    }
                });

        ChannelFuture channelFuture = serverBootstrap.bind(port);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                logger.info("Server start in port:" + port);
                serviceRegister.serviceRegister();
            }
        });

        channelFuture.channel().closeFuture().sync();
    }


    //构造器
    public static class Builder{
        private static Logger logger = LoggerFactory.getLogger(Builder.class);

        //Required parameters
        private String zookeeperHost;
        private int port;
        private Class interfaceClass;
        private Object interfaceImpl;

        //Optional parameters
        private int weight = 1;
        private int maxRequestMessageLength = 65535;

        public Builder(String zookeeperHost, int port, Class interfaceClass, Object interfaceImpl){
            this.zookeeperHost = zookeeperHost;
            this.port = port;
            this.interfaceClass = interfaceClass;
            if(!interfaceClass.isInstance(interfaceImpl)){
                logger.error(interfaceImpl.getClass().getName() + "not instance of " + interfaceClass.getName());
            } else {
                this.interfaceImpl = interfaceImpl;
            }
        }

        public Builder(String zookeeperHost, int port, String interfaceClassName, Object interfaceImpl){
            this.zookeeperHost = zookeeperHost;
            this.port = port;
            try {
                this.interfaceClass = Class.forName(interfaceClassName);
                if(!interfaceClass.isInstance(interfaceImpl)){
                    logger.error(interfaceImpl.getClass().getName() +
                            "not instance of " + interfaceClass.getName());
                } else {
                    this.interfaceImpl = interfaceImpl;
                }
            } catch (ClassNotFoundException e) {
                logger.error("Can't found class:" + interfaceClassName + " error:" + e.getMessage());
            }
        }

        public Builder weight(int weight){
            this.weight = weight;
            return this;
        }
        public Builder maxRequestMessageLength(int maxLength){
            this.maxRequestMessageLength = maxLength;
            return this;
        }

        public ProviderPublisher build(){
            return new ProviderPublisher(this);
        }
    }
}
