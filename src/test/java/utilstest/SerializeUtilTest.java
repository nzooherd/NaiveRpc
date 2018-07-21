package utilstest;

import gq.shiwenhao.naiverpc.entities.RpcRequest;
import gq.shiwenhao.naiverpc.utils.SerializeUtil;
import org.junit.Test;

public class SerializeUtilTest {

    @Test
    public void serializeTest(){
        RpcRequest rpcRequest = new RpcRequest();
        byte[] bytes = SerializeUtil.writeToByteArray(rpcRequest);
        RpcRequest rpcRequest_ = SerializeUtil.readFromByteArray(bytes);
        assert (rpcRequest.equals(rpcRequest_));
    }
}
