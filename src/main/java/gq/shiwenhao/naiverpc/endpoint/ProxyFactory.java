package gq.shiwenhao.naiverpc.endpoint;

import gq.shiwenhao.naiverpc.entities.ProviderHost;
import gq.shiwenhao.naiverpc.entities.RpcFuture;
import gq.shiwenhao.naiverpc.entities.RpcRequest;
import gq.shiwenhao.naiverpc.servicegovern.ServiceDiscover;
import gq.shiwenhao.naiverpc.transport.ConnectManager;
import gq.shiwenhao.naiverpc.transport.RpcRequestHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyFactory<T> implements InvocationHandler {
    private Class<T> interfaceClass;
    private ServiceDiscover serviceDiscover;


    public ProxyFactory(Class<T> interfaceClass, ServiceDiscover serviceDiscover){
        this.interfaceClass = interfaceClass;
        this.serviceDiscover = serviceDiscover;
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
        RpcRequest rpcRequest = createRpcRequest(method.getDeclaringClass().getName(),
                    method.getName(), args);
        ProviderHost providerHost = serviceDiscover.serviceLoadBalance();
        RpcRequestHandler clientHandler = ConnectManager.getInstance().
                getChannelByProviderHost(providerHost);

        return null;
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
