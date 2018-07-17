package gq.shiwenhao.naiverpc.transport;

import gq.shiwenhao.naiverpc.entities.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {

    }
}
