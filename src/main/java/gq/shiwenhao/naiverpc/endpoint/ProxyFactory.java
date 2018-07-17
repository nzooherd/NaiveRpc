package gq.shiwenhao.naiverpc.endpoint;

import gq.shiwenhao.naiverpc.entities.ProviderHost;
import gq.shiwenhao.naiverpc.transport.RpcFuture;
import gq.shiwenhao.naiverpc.entities.RpcRequest;
import gq.shiwenhao.naiverpc.servicegovern.ServiceDiscover;
import gq.shiwenhao.naiverpc.transport.ConnectManager;
import gq.shiwenhao.naiverpc.transport.RpcRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyFactory implements InvocationHandler {
    private Logger logger = LoggerFactory.getLogger("ProxyFactory.class");

    private Class interfaceClass;
    private ServiceDiscover serviceDiscover;

    private boolean retryRequest;
    private int retryTimes = 1;
    private int timeout = 2000;

    public ProxyFactory(Class<?> interfaceClass, ServiceDiscover serviceDiscover){
        this.interfaceClass = interfaceClass;
        this.serviceDiscover = serviceDiscover;
    }

    public void setTimeControl(boolean retryRequest, int retryTimes, int timeout){
        this.retryRequest =retryRequest;
        if(retryRequest && retryTimes <= 0 ){
            logger.error("RetryTimes be positive number");
        }
        this.retryTimes = retryTimes;
        this.timeout = timeout;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }

        int attemptCounts = 0;
        Object result = null;
        RpcRequest rpcRequest = createRpcRequest(method.getDeclaringClass().getName(),
                    method.getName(), args);

        while(result == null && attemptCounts++ < retryTimes) {
            ProviderHost providerHost = serviceDiscover.serviceLoadBalance();
            RpcRequestHandler clientHandler = ConnectManager.getInstance().
                    getChannelByProviderHost(providerHost);
            RpcFuture rpcFuture = clientHandler.sendRequest(rpcRequest);
            if(retryRequest) rpcFuture.timeControl(timeout);
            result = rpcFuture.get();
        }

        if(result == null) {
            logger.error("RequestId: ");
        }
        return result;
    }

    public Object call(String methodName, Object[] args){
        RpcRequest rpcRequest = createRpcRequest(interfaceClass.getName(), methodName, args);
        ProviderHost providerHost = serviceDiscover.serviceLoadBalance();
        RpcRequestHandler clientHandler = ConnectManager.getInstance().
                getChannelByProviderHost(providerHost);
        RpcFuture rpcFuture = clientHandler.sendRequest(rpcRequest);
        return rpcFuture;
    }

    private RpcRequest createRpcRequest(String className, String methodName, Object[] args){
        RpcRequest rpcRequest = new RpcRequest();

        rpcRequest.setClassName(className);
        rpcRequest.setMethodName(methodName);

        Class[] paramTypes = new Class[args.length];
        for(int i = 0; i < args.length; i++) {
            paramTypes[i] = getClassType(args[i]);
        }
        rpcRequest.setParamTypes(paramTypes);
        rpcRequest.setArguments(args);

        return rpcRequest;

    }

    private Class<?> getClassType(Object obj) {
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        switch (typeName) {
            case "java.lang.Integer":
                return Integer.TYPE;
            case "java.lang.Long":
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double":
                return Double.TYPE;
            case "java.lang.Character":
                return Character.TYPE;
            case "java.lang.Boolean":
                return Boolean.TYPE;
            case "java.lang.Short":
                return Short.TYPE;
            case "java.lang.Byte":
                return Byte.TYPE;
        }
        return classType;
    }
}
