package gq.shiwenhao.naiverpc.transport;

import gq.shiwenhao.naiverpc.entities.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);

    private Map<Long, RpcResponse> rpcResult = new ConcurrentHashMap<Long, RpcResponse>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        long request_id = msg.getRequestId();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.error("Client caught exception " + cause);
        ctx.close();
    }



}
