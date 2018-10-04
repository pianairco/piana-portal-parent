package ir.piana.dev.portal.web.secure;

import ir.piana.dev.portal.web.module.config.Session;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Mohammad Rahmati, 10/3/2018
 */
public class PianaSecure<T> {
    private static Map<String, PianaSessionManager> sessionManagerMap = new LinkedHashMap<>();

    private PianaSecure() {

    }

    public static synchronized PianaSessionManager createInstance(
            String serverName,
            Session session) {
        if(sessionManagerMap.get(serverName) == null) {
            sessionManagerMap.put(serverName, PianaSessionManager.getSessionManager(session));
        }

        return sessionManagerMap.get(serverName);
    }

    public static PianaSessionManager getSessionManager(String serverName) {
        return sessionManagerMap.get(serverName);
    }
}
