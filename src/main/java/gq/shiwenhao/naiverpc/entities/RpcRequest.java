package gq.shiwenhao.naiverpc.entities;


import java.util.concurrent.atomic.AtomicLong;

public class RpcRequest {
    private static final AtomicLong REQUEST_ID = new AtomicLong(1L);

    private long requestId;

    private String className;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] arguments;

    public RpcRequest(){
        requestId = REQUEST_ID.getAndIncrement();
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getClassName(){
        return className;
    }
    public void setClassName(String className){
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }


    @Override
    public int hashCode(){
        return (int)requestId & 0x8ffffff;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof RpcRequest){
            return requestId == ((RpcRequest) o).getRequestId();
        }
        return false;
    }
}
