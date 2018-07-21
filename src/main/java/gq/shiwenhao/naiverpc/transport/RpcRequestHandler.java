package gq.shiwenhao.naiverpc.transport;

import gq.shiwenhao.naiverpc.entities.RpcRequest;
import gq.shiwenhao.naiverpc.entities.RpcResponse;
import gq.shiwenhao.naiverpc.utils.SerializeUtil;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;


public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);

    private volatile Channel channel;
    private Map<Long, RpcFuture> pendingRpc = new ConcurrentHashMap<Long, RpcFuture>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

    }
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception{
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        logger.debug(ctx.getClass().getName());

        long request_id = msg.getRequestId();
        RpcFuture rpcFuture = pendingRpc.get(request_id);
        rpcFuture.set(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.error("Client caught exception " + cause);
        ctx.close();
    }

    public RpcFuture sendRequest(RpcRequest request){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        RpcFuture rpcFuture = new RpcFuture(this);
        pendingRpc.put(request.getRequestId(), rpcFuture);
        channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return rpcFuture;
    }

    public void removeRequest(long requestId){
        pendingRpc.remove(requestId);
    }


}
