package ir.piana.dev.portal.web;

import ir.piana.dev.portal.web.error.CriticalError;
import ir.piana.dev.portal.web.http.GrizzlyHttpServer;
import org.apache.commons.io.IOUtils;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.Header;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Mohammad Rahmati, 9/29/2018
 */
public class PianaPortalTerter {
    public static void main(String[] args) throws IOException, CriticalError {
        GrizzlyHttpServer grizzlyHttpServer = new GrizzlyHttpServer("http-server-1");
        NetworkListener listener = new NetworkListener("redirect-listener", "localhost", 8080);
        grizzlyHttpServer.addNetworkListener(listener);
        grizzlyHttpServer.addHttpHandler(new HttpHandler() {
            @Override
            public void service(Request request, Response response) throws Exception {
                System.out.println("request.getContextPath()");
                System.out.println(request.getRequestURI());
                System.out.println(request.getRequestURL());
                System.out.println(request.getRequest().getRequestURI());
                System.out.println(request.getRequest().getQueryStringDC());
                System.out.println(request.getRequest().getQueryString());
                response.setHeader(Header.Location, "https://localhost:9090"
                        .concat(request.getRequest().getRequestURI())
                        .concat(request.getRequest().getQueryString()));
                response.setStatus(HttpStatus.MOVED_PERMANENTLY_301);
            }
        });
        grizzlyHttpServer.startService();

        SSLContextConfigurator sslCon = new SSLContextConfigurator();
        InputStream is = PianaPortalServer.class.getResourceAsStream("/keystore.jks");
        byte[] bytes = IOUtils.toByteArray(is);
        sslCon.setKeyStoreBytes(bytes); // contains web keypair
        sslCon.setKeyStorePass("password");

        GrizzlyHttpServer grizzlyHttpServer2 = new GrizzlyHttpServer("http-server-2");
        NetworkListener listener2 = new NetworkListener("redirect-listener", "localhost", 9090);
        listener2.setSecure(true);
        listener2.setSSLEngineConfig(new SSLEngineConfigurator(sslCon).setClientMode(false).setNeedClientAuth(false));

        grizzlyHttpServer2.addNetworkListener(listener2);
        grizzlyHttpServer2.addHttpHandler(new HttpHandler() {
            @Override
            public void service(Request request, Response response) throws Exception {
                response.setStatus(HttpStatus.OK_200);
            }
        }, "/hello");
        grizzlyHttpServer2.startService();

        System.out.println("Press any key to stop the web...");
        System.in.read();
    }
}
