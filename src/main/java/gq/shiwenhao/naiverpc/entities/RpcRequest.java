package gq.shiwenhao.naiverpc.entities;

import java.lang.reflect.Method;

public class RpcRequest {

    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] arguments;

}
