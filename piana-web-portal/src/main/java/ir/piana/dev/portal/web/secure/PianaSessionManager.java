package ir.piana.dev.portal.web.secure;

import ir.piana.dev.portal.web.module.config.Session;
import ir.piana.dev.secure.PianaSecureException;
import ir.piana.dev.secure.cache.PianaCacheProvider;
import ir.piana.dev.secure.key.KeyPairAlgorithm;
import ir.piana.dev.secure.key.KeyPairMaker;
import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.Cookie;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import java.util.UUID;

/**
 * @author Mohammad Rahmati, 4/29/2017 2:15 PM
 */
public class PianaSessionManager {
    final static Logger logger =
            Logger.getLogger(PianaSessionManager.class);
    public static final String PIANA_SESSION_MANAGER =
            "piana-session-manager";
    private PianaCacheProvider cacheProvider = null;
    protected Session session;

    protected static PianaSessionManager pianaSessionManager = null;

    private PianaSessionManager(
            Session session) {
        this.session = session;
        cacheProvider =
                PianaCacheProvider.getInstance(
                        key -> createNewSession(session),
                        session.getCacheSize(),
                        session.getExpiredSecond());
    }

    public static synchronized PianaSessionManager getSessionManager(
            Session serverSession) {
        if(pianaSessionManager != null)
            return pianaSessionManager;
        if (serverSession == null)
            return null;
        pianaSessionManager = new PianaSessionManager(
                serverSession);
        return pianaSessionManager;
    }

//    public PianaSession revivalSession(
//            Request request, Response response) {
//        PianaSession pianaSession = null;
//        try {
//            Cookie sessionCookie = null;
//            for(Cookie cookie : request.getCookies()) {
//                if(cookie.getName().equalsIgnoreCase(session.getName())) {
//                    sessionCookie = cookie;
//                    break;
//                }
//            }
//            if(sessionCookie == null ||
//                    sessionCookie.getValue() == null ||
//                    sessionCookie.getValue().isEmpty())
//                return null;
//            pianaSession = (PianaSession) cacheProvider
//                    .retrieveIfExist(sessionCookie.getValue());
//            response.addCookie(sessionCookie);
//        } catch (Exception e) {
//            logger.info(e.getMessage());
//        }
//        return pianaSession;
//    }

    public PianaSession retrieveSession(
            Request request, Response response) {
        PianaSession pianaSession = null;
        String sessionKey = null;
        try {
            sessionKey = request.getHeader(session.getName());
            if(sessionKey == null || sessionKey.isEmpty()) {
                sessionKey = createSessionKey();
            }
            pianaSession = (PianaSession) cacheProvider
                    .retrieve(sessionKey);
            pianaSession.setSessionKey(sessionKey);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        response.addHeader(session.getName(), sessionKey);
        return pianaSession;
    }

//    public PianaSession retrieveSessionIfExist(
//            String sessionKey) throws Exception {
//        return  (PianaSession) cacheProvider
//                        .retrieveIfExist(sessionKey);
//    }

    public Cookie makeSessionCookie(
            PianaSession pianaSession) {
        if(pianaSession == null) {
            logger.info("pianaSession is null.");
            return null;
        }
        Cookie cookie = new Cookie(
                session.getName(), pianaSession.getSessionKey());
        cookie.setDomain("");
        cookie.setPath("/");
        cookie.setComment(null);
        cookie.setMaxAge(session.getExpiredSecond());
        cookie.setSecure(false);
        cookie.setHttpOnly(false);
        return cookie;
    }

    private static PianaSession createNewSession(
            Session serverSession)
            throws PianaSecureException {
        return new PianaSession(
                serverSession.getName(),
                KeyPairMaker.createKeyPair(
                        KeyPairAlgorithm.RSA_1024));
    }

    private static String createSessionKey() {
        return UUID.randomUUID().toString();
    }

}
