package ir.piana.dev.portal.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.glassfish.grizzly.http.server.*;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PianaPortalServer {

    public static void main(String[] args) {
        HttpServer server = HttpServer.createSimpleServer();

        ServerConfiguration serverConfiguration = server.getServerConfiguration();
        StaticHttpHandler staticHandler = new StaticHttpHandler(
                "E:\\000-Dadekavan\\products\\geo-fence\\geo-fence\\notify-ui\\dist\\notify-ui");
        serverConfiguration.addHttpHandler(staticHandler);
        serverConfiguration.addHttpHandler(
                new HttpHandler() {
                    public void service(Request request, Response response) throws Exception {
                        File folder = new File("E:\\000-Dadekavan\\products\\geo-fence\\geo-fence\\notify-ui\\dist\\notify-ui");
                        File[] listOfFiles = folder.listFiles();

                        List<String> fileNames = new ArrayList<>();
                        for (int i = 0; i < 5; i++) {
                            if (listOfFiles[i].isFile()) {
                                fileNames.add("File " + listOfFiles[i].getName());
                            } else if (listOfFiles[i].isDirectory()) {
                                fileNames.add("Directory " + listOfFiles[i].getName());
                            }
                        }

                        response.setContentType("application/json;charset=UTF-8");
                        ObjectMapper objectMapper = new ObjectMapper();
                        String data = objectMapper.writeValueAsString(fileNames);
                        response.setContentLength(data.length());
                        response.addHeader("Access-Control-Allow-Origin", "*");
                        response.addHeader("Access-Control-Allow-Method", "GET, POST, PUT");
                        response.addHeader("Access-Control-Allow-Header", "Content-Type");

                        response.getWriter().write(data);
                    }
                },
                "/file-name");

        try {
            SSLContextConfigurator sslCon = new SSLContextConfigurator();
            InputStream is = PianaPortalServer.class.getResourceAsStream("/keystore.jks");
            byte[] bytes = IOUtils.toByteArray(is);
            sslCon.setKeyStoreBytes(bytes); // contains web keypair
            sslCon.setKeyStorePass("password");

//            for(NetworkListener listener : web.getListeners()) {
//                listener.setSecure(true);
//                listener.setSSLEngineConfig(new SSLEngineConfigurator(sslCon).setClientMode(false).setNeedClientAuth(false));
//            }

            NetworkListener timeListener = new NetworkListener("TimeListener", "localhost", 9090);
            timeListener.setSSLEngineConfig(new SSLEngineConfigurator(sslCon));
            timeListener.setSecure(true);

            server.start();
            System.out.println("Press any key to stop the web...");
            System.in.read();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
