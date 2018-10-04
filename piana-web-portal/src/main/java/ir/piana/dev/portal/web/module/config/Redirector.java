package ir.piana.dev.portal.web.module.config;

/**
 * @author Mohammad Rahmati, 9/29/2018
 */
public class Redirector {
    private String name;
    private String host;
    private String port;
    private SSLConfig sslConfig;
    private String toSerevr;
    private String toListener;

    public Redirector() {
    }

    public Redirector(
            String name, String host, String port,
            SSLConfig sslConfig,
            String toServer, String toListener) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.sslConfig = sslConfig;
        this.toSerevr = toServer;
        this.toListener = toListener;
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

    public String getToSerevr() {
        return toSerevr;
    }

    public void setToSerevr(String toSerevr) {
        this.toSerevr = toSerevr;
    }

    public String getToListener() {
        return toListener;
    }

    public void setToListener(String toListener) {
        this.toListener = toListener;
    }

    public SSLConfig getSslConfig() {
        return sslConfig;
    }

    public void setSslConfig(SSLConfig sslConfig) {
        this.sslConfig = sslConfig;
    }
}
