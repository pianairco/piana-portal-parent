package ir.piana.dev.portal.web.response;

/**
 * @author Mohammad Rahmati, 10/3/2018
 */
public class PianaResponse<T> {
    private int status;
    private T entity;

    public PianaResponse() {
        status = 0;
    }

    public PianaResponse(int status) {
        this.status = status;
    }

    public PianaResponse(int status, T entity) {
        this.status = status;
        this.entity = entity;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }
}
