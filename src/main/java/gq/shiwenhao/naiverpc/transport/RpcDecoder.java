package gq.shiwenhao.naiverpc.transport;

import gq.shiwenhao.naiverpc.utils.SerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {
    private static Logger logger = LoggerFactory.getLogger(RpcDecoder.class);
    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int dataLength = in.readableBytes();
        byte[] bytes = new byte[dataLength];
        in.readBytes(bytes);

        Object obj = SerializeUtil.readFromByteArray(bytes);
        if(!genericClass.isInstance(obj)){
            logger.error("Object generate from byte not instance " + genericClass.getName());
            return;
        }

        out.add(obj);
    }
}
