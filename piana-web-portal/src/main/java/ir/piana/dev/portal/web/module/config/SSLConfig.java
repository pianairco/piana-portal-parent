package ir.piana.dev.portal.web.module.config;

/**
 * @author Mohammad Rahmati, 9/29/2018
 */
public class SSLConfig {
    private String keyStorePath;
    private String keyStorePass;

    public SSLConfig() {
    }

    public SSLConfig(String keyStorePath, String keyStorePass) {
        this.keyStorePath = keyStorePath;
        this.keyStorePass = keyStorePass;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath) {
        this.keyStorePath = keyStorePath;
    }

    public String getKeyStorePass() {
        return keyStorePass;
    }

    public void setKeyStorePass(String keyStorePass) {
        this.keyStorePass = keyStorePass;
    }
}
