package ir.piana.dev.portal.web.module.config;

/**
 * @author Mohammad Rahmati, 9/29/2018
 */
public class Session {
    private String name;
    private int cacheSize;
    private int expiredSecond;

    public Session() {
    }

    public Session(String name, int cacheSize, int expiredSecond) {
        this.name = name;
        this.cacheSize = cacheSize;
        this.expiredSecond = expiredSecond;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public int getExpiredSecond() {
        return expiredSecond;
    }

    public void setExpiredSecond(int expiredSecond) {
        this.expiredSecond = expiredSecond;
    }
}
