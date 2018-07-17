package gq.shiwenhao.naiverpc.transport;

import gq.shiwenhao.naiverpc.entities.RpcRequest;
import gq.shiwenhao.naiverpc.utils.ServerProcessPool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private Logger logger = LoggerFactory.getLogger(RpcResponseHandler.class);

    private Object interfaceImpl;
    private Class<?> interfaceClass;

    private ServerProcessPool serverProcessPool;


    public RpcResponseHandler(Class interfaceClass, Object interfaceImpl){
        this.interfaceClass = interfaceClass;
        this.interfaceImpl = interfaceImpl;

        serverProcessPool = ServerProcessPool.getInstance();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        serverProcessPool.process(ctx, msg, interfaceImpl);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.error("server caught exception", cause);
        ctx.close();
    }

}
