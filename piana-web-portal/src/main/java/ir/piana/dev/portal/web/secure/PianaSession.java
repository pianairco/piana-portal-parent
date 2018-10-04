package ir.piana.dev.portal.web.secure;

import ir.piana.dev.secure.crypto.CryptoAttribute;
import ir.piana.dev.secure.crypto.CryptoMaker;

import java.security.KeyPair;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Mohammad Rahmati, 4/18/2017 4:30 PM
 */
public class PianaSession<T> {
    private T existance;
    private boolean wrongdoer;
    private String sessionName;
    private KeyPair keyPair;

    private String sessionKey;
    private Map<String, String> stringMap;
    private Map<String, Object> objectMap;

    PianaSession(String sessionName,
                 KeyPair keyPair) {
        this(sessionName, keyPair, null);
    }

    PianaSession(String sessionName,
                 KeyPair keyPair,
                 String sessionKey) {
        this.sessionName = sessionName;
        this.keyPair = keyPair;
        this.sessionKey = sessionKey;
        this.stringMap = new LinkedHashMap<>();
        this.objectMap = new LinkedHashMap<>();
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public byte[] getPublicKeyBytes() {
        return keyPair.getPublic().getEncoded();
    }

    public void setString(String key, String value) {
        this.stringMap.put(key, value);
    }

    public String getString(String key) {
        return this.stringMap.get(key);
    }

    public String removeString(String key) {
        return this.stringMap.remove(key);
    }

    public void clearString() {
        this.stringMap.clear();
    }

    public void setObject(String key, Object value) {
        this.objectMap.put(key, value);
    }

    public Object getObject(String key) {
        return this.objectMap.get(key);
    }

    public Object removeObject(String key) {
        return this.objectMap.remove(key);
    }

    public void clearObject() {
        this.objectMap.clear();
    }

    public byte[] decrypt(byte[] rawMessage)
            throws Exception {
        return CryptoMaker.decrypt(rawMessage,
                keyPair.getPrivate(),
                CryptoAttribute.RSA);
    }

    public Boolean isWrongdoer() {
        return wrongdoer;
    }

    public void setWrongdoer(Boolean wrongdoer) {
        this.wrongdoer = wrongdoer;
    }

    public T getExistance() {
        return existance;
    }

    public void setExistance(T existance) {
        this.existance = existance;
    }
}
