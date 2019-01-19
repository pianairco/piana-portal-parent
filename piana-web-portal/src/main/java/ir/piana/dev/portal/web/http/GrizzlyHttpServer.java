package ir.piana.dev.portal.web.http;

import ir.piana.dev.portal.web.error.CriticalError;
import org.apache.log4j.Logger;
import org.glassfish.grizzly.GrizzlyFuture;
import org.glassfish.grizzly.http.server.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Mohammad Rahmati, 4/23/2017 11:36 AM
 */
public class GrizzlyHttpServer {
    protected Logger logger = Logger
            .getLogger(GrizzlyHttpServer.class);
    protected HttpServer httpServer;
    protected String name;

    public GrizzlyHttpServer(String name) {
        this.name = name;
        httpServer = new HttpServer();
        httpServer.getServerConfiguration().setSessionManager(DefaultSessionManager.instance());
        SessionManager sessionManager = httpServer.getServerConfiguration().getSessionManager();
        System.out.println(sessionManager);
    }

    public void startService() throws CriticalError {
        if(!httpServer.isStarted()) {
            try {
                httpServer.start();
            } catch (IOException e) {
                logger.error(e.getMessage());
                throw new CriticalError(e.getMessage());
            }
        }
    }

    public GrizzlyFuture<HttpServer> stopService() throws CriticalError {
        if(!httpServer.isStarted())
            return null;
        GrizzlyFuture<HttpServer> future = httpServer.shutdown(10, TimeUnit.SECONDS);
        logger.info(String.format("Waiting for server '%s' to shut down... Grace period is %s %s",
                name, "10", "second"));
        return future;
    }

    public void addNetworkListener(NetworkListener networkListener) {
        httpServer.addListener(networkListener);
    }

    public void addHttpHandler(HttpHandler handler) {
        httpServer.getServerConfiguration().addHttpHandler(handler);
    }

    public void addHttpHandler(HttpHandler handler, String... mappings) {
        httpServer.getServerConfiguration().addHttpHandler(handler, mappings);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
