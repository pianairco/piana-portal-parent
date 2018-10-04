package ir.piana.dev.portal.web.module.config;

/**
 * @author Mohammad Rahmati, 9/29/2018
 */
public class Listener {
    private String name;
    private String host;
    private String port;
    private SSLConfig sslConfig;

    public Listener() {
    }

    public Listener(String name, String host, String port, SSLConfig sslConfig) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.sslConfig = sslConfig;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return Integer.parseInt(port);
    }

    public void setPort(String port) {
        this.port = port;
    }

    public SSLConfig getSslConfig() {
        return sslConfig;
    }

    public void setSslConfig(SSLConfig sslConfig) {
        this.sslConfig = sslConfig;
    }
}
