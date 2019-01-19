package ir.piana.dev.portal.web.handler;

import ir.piana.dev.portal.web.secure.PianaSecure;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.StaticHttpHandler;

import java.util.Map;
import java.util.Set;

/**
 * @author Mohammad Rahmati, 10/3/2018
 */
public class PianaHttpHandlerStatic extends StaticHttpHandler
        implements PianaHttpHandlerNameInjectable {
    protected String serverName;

    public PianaHttpHandlerStatic() {
        super();
    }

    public PianaHttpHandlerStatic(String... docRoots) {
        super(docRoots);
    }

    public PianaHttpHandlerStatic(Set<String> docRoots) {
        super(docRoots);
    }

    @Override
    public final void service(final Request request, final Response response)
            throws Exception {
//        PianaSecure.getSessionManager(serverName)
//                .retrieveSession(request, response);
        super.service(request, response);
    }

    @Override
    public final void injectServerName(String serverName) {
        this.serverName = serverName;
    }
}
