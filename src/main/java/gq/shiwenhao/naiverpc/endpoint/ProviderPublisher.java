package gq.shiwenhao.naiverpc.endpoint;

import gq.shiwenhao.naiverpc.entities.RpcRequest;
import gq.shiwenhao.naiverpc.entities.RpcResponse;
import gq.shiwenhao.naiverpc.servicegovern.ServiceRegister;
import gq.shiwenhao.naiverpc.servicegovern.ZookeeperManager;
import gq.shiwenhao.naiverpc.transport.RpcDecoder;
import gq.shiwenhao.naiverpc.transport.RpcEncoder;
import gq.shiwenhao.naiverpc.transport.RpcResponseHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProviderPublisher {
    private Logger logger = LoggerFactory.getLogger(ProviderPublisher.class);

    private Class interfaceClass;
    private Object interfaceImpl;
    private String zookeeperHost;
    private int port;

    private ZookeeperManager zookeeperManager;
    private ServiceRegister serviceRegister;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    public ProviderPublisher(Class interfaceClass, Object interfaceImpl, String zookeeperHost, int port){
        if(!interfaceClass.isInstance(interfaceImpl)) {
            logger.error(interfaceImpl.getClass().getName() + " not instance of " + interfaceClass.getName());
            return;
        }

        this.interfaceClass = interfaceClass;
        this.interfaceImpl = interfaceImpl;
        this.zookeeperHost = zookeeperHost;
        this.port = port;

        zookeeperManager = new ZookeeperManager(zookeeperHost);
        serviceRegister = new ServiceRegister(zookeeperManager, interfaceClass, port);
    }

    private void start() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65536,
                                0, 4, 0, 4));
                        ch.pipeline().addLast(new RpcEncoder(RpcResponse.class));
                        ch.pipeline().addLast(new RpcDecoder(RpcRequest.class));
                        ch.pipeline().addLast(new RpcResponseHandler());
                    }
                });

        ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
        logger.info("Server start in port:" + port);
        serviceRegister.serviceRegister();

        channelFuture.channel().closeFuture().sync();
    }
}
