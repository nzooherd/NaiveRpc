package gq.shiwenhao.naiverpc.utils;

import gq.shiwenhao.naiverpc.entities.RpcRequest;
import gq.shiwenhao.naiverpc.entities.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.*;

public class ServerProcessPool {
    private Logger logger = LoggerFactory.getLogger(ServerProcessPool.class);

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(16, 16, 600L,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
    private static volatile ServerProcessPool serverProcessPool;

    private ServerProcessPool(){

    }

    public static ServerProcessPool getInstance(){
        if(serverProcessPool == null){
            synchronized (ServerProcessPool.class){
                if(serverProcessPool == null) {
                    serverProcessPool = new ServerProcessPool();
                }
            }
            return serverProcessPool;
        }
        return serverProcessPool;
    }

    public void process(ChannelHandlerContext ctx, RpcRequest rpcRequest,
                        Object interfaceImpl){
        String methodName = rpcRequest.getMethodName();
        Class[] paramTypes = rpcRequest.getParamTypes();
        Object[] arguments = rpcRequest.getArguments();
        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequest.getRequestId());

        try {
            Method method = interfaceImpl.getClass().getMethod(methodName, paramTypes);
            Future<RpcResponse> future = executor.submit(new MethodInvoke(
                    method, interfaceImpl, arguments, response));
            response = future.get();
        } catch (NoSuchMethodException e) {
            logger.warn("No method:" + e.getMessage());
        } catch (InterruptedException | ExecutionException e){
            response.setError(e);
            logger.error("Execute request:" + rpcRequest.getRequestId() + " error:" + e.getMessage());
        } finally {
            response.setRequestId(rpcRequest.getRequestId());

            ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    logger.info("Send response for request " + rpcRequest.getRequestId());
                }
            });
        }

    }


    private class MethodInvoke implements Callable<RpcResponse> {
        private Method method;
        private Object interfaceImpl;
        private Object[] arguments;

        private RpcResponse response;

        private MethodInvoke(Method method, Object interfaceImpl, Object[] arguments, RpcResponse response){
            this.method = method;
            this.interfaceImpl = interfaceImpl;
            this.arguments = arguments;

            this.response = response;
        }

        @Override
        public RpcResponse call() throws Exception {
            logger.debug("Request:" + response.getRequestId() + "start process");

            Object ret = method.invoke(interfaceImpl, arguments);
            response.setResult(ret);

            logger.debug("Request:" + response.getRequestId() + "end process");
            return response;
        }
    }
}
