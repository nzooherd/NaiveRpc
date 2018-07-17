package gq.shiwenhao.naiverpc.entities;

public class RpcResponse {
    private long requestId;
    private Throwable error;
    private Object result;

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public boolean isError(){
        return error != null;
    }
}
