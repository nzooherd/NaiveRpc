package gq.shiwenhao.naiverpc.transport;

import gq.shiwenhao.naiverpc.utils.SerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcEncoder extends MessageToByteEncoder {
    private static Logger logger = LoggerFactory.getLogger(RpcEncoder.class);

    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(!genericClass.isInstance(msg)){
            logger.error("Object msg not instance " + genericClass.getName());
            return;
        }

        byte[] bytes = SerializeUtil.writeToByteArray(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);

    }
}
