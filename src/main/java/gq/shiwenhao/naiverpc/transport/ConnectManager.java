package gq.shiwenhao.naiverpc.transport;

import gq.shiwenhao.naiverpc.entities.ProviderHost;
import gq.shiwenhao.naiverpc.entities.RpcRequest;
import gq.shiwenhao.naiverpc.entities.RpcResponse;
import gq.shiwenhao.naiverpc.servicegovern.ServiceDiscover;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;

public class ConnectManager {
    private static Logger logger = LoggerFactory.getLogger(ConnectManager.class);

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(16, 16, 600L,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));

    private EventLoopGroup loopGroup = new NioEventLoopGroup();

    //池化Socket
    private Map<ProviderHost, RpcRequestHandler> connectedServerNodes = new ConcurrentHashMap<>();

    private static volatile ConnectManager connectManager;
    private ServiceDiscover serviceDiscover;

    private ConnectManager(ServiceDiscover serviceDiscover){
        this.serviceDiscover = serviceDiscover;
    }

    public static ConnectManager getInstance(ServiceDiscover serviceDiscover){
        if(connectManager == null) {
            synchronized (ConnectManager.class){
                if(connectManager == null){
                    connectManager = new ConnectManager(serviceDiscover);
                }
                return connectManager;
            }
        }
        return connectManager;
    }

    public void connectServerNode(ProviderHost providerHost){
        executor.submit(() -> {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535,
                                    0, 4, 0, 4));
                            ch.pipeline().addLast(new RpcEncoder(RpcRequest.class));
                            ch.pipeline().addLast(new RpcDecoder(RpcResponse.class));
                            ch.pipeline().addLast(new RpcRequestHandler());
                        }
                    });

            logger.info("Start connect to provider:" + providerHost.toString());
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(
                                    providerHost.getHost(), providerHost.getPort()));
            channelFuture.addListener((ChannelFutureListener) future -> {
                logger.info("Connect to provider:" + providerHost.toString() + " success" );
                RpcRequestHandler clientHandler = future.channel().
                        pipeline().get(RpcRequestHandler.class);
                serviceDiscover.addConnectNode(providerHost);
                connectedServerNodes.put(providerHost, clientHandler);
                serviceDiscover.countDown();
            });
        });
    }

    public void addServerNode(ProviderHost providerHost){
        connectServerNode(providerHost);
        logger.info("Provider:" + providerHost.toString() + " was add");
    }
    public void removeServerNode(ProviderHost providerHost){
        connectedServerNodes.remove(providerHost);
        logger.info("Provider:" + providerHost.toString() + " was remove");
    }
    public void updateServerNode(ProviderHost providerHost){
        RpcRequestHandler clientHandler = connectedServerNodes.get(providerHost);
        connectedServerNodes.remove(providerHost);
        connectedServerNodes.put(providerHost, clientHandler);
        logger.info("Provider:" + providerHost.toString() + " was update");
    }


    public RpcRequestHandler getChannelByProviderHost(ProviderHost host){
        return connectedServerNodes.get(host);
    }


}
