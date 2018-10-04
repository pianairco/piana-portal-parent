package ir.piana.dev.portal.web.secure;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

/**
 * @author Mohammad Rahmati, 10/4/2018
 */
public class PianaAuthorizer {
    public static void authorize(
            String serverName,
            Request request,
            Response response,
            String authorizationKey) {
        PianaSessionManager sessionManager = PianaSecure.getSessionManager(serverName);
        PianaSession pianaSession = sessionManager.retrieveSession(request, response);
        pianaSession.setExistance(authorizationKey);
    }

    public static void clear(
            String serverName,
            Request request,
            Response response) {
        PianaSessionManager sessionManager = PianaSecure.getSessionManager(serverName);
        PianaSession pianaSession = sessionManager.retrieveSession(request, response);
        pianaSession.setExistance(null);

    }
}
