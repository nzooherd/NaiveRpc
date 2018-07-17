package gq.shiwenhao.naiverpc.transport;

import gq.shiwenhao.naiverpc.entities.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RpcFuture {
    private Logger logger = LoggerFactory.getLogger(RpcFuture.class);

    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private RpcResponse response;
    private RpcRequestHandler clientHandler;

    private int timeout;

    public RpcFuture(RpcRequestHandler clientHandler){
        this.clientHandler = clientHandler;
    }

    public void timeControl(int timeout){
        this.timeout = timeout;
    }

    public Object get(){
        try {
            if(timeout <= 0) countDownLatch.await();
            else {
                boolean timeFlag =  countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
                if(!timeFlag){
                    return null;
                }
            }
        } catch (InterruptedException e) {
            logger.error("Rpc future try to get response throw exception:" + e.getMessage());
        }
        clientHandler.removeRequest(response.getRequestId());
        return response;
    }

    public void set(RpcResponse response){
        this.response = response;
        countDownLatch.countDown();
    }

}
