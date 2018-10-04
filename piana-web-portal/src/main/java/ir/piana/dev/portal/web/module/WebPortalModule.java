package ir.piana.dev.portal.web.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.piana.dev.portal.web.handler.*;
import ir.piana.dev.portal.web.http.GrizzlyHttpServer;
import ir.piana.dev.portal.web.module.config.*;
import ir.piana.dev.portal.web.secure.PianaRoleProvidable;
import ir.piana.dev.portal.web.secure.PianaSecure;
import org.apache.log4j.Logger;
import org.glassfish.grizzly.GrizzlyFuture;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.jdom2.Element;
import org.jpos.core.ConfigurationException;
import org.jpos.core.XmlConfigurable;
import org.jpos.q2.QBeanSupport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author Mohammad Rahmati, 9/29/2018
 */
public class WebPortalModule extends QBeanSupport implements XmlConfigurable {
    protected Logger logger = Logger
            .getLogger(WebPortalModule.class);
    private List<Server> serverList = new ArrayList<>();
    private List<Redirector> redirectorList = new ArrayList<>();
    private List<GrizzlyHttpServer> httpServerList = new ArrayList<>();

    protected void initService() throws Exception {
        System.out.println("init");
        for(Server server : serverList) {
            GrizzlyHttpServer httpServer = new GrizzlyHttpServer(server.getName());
            JsonNode jsonNode = readMapping(server.getName().concat(".json"));
            setAssetHandler(httpServer, jsonNode.path("asset-handlers"));

            PianaRoleProvidable roleProvidable = null;
            if(server.getPianaRoleProvidable() != null && !server.getPianaRoleProvidable().isEmpty()) {
                roleProvidable = (PianaRoleProvidable)Class.forName(
                        server.getPianaRoleProvidable()).newInstance();
            } else {
                roleProvidable = new PianaRoleProvidable() {
                    @Override
                    public List<String> provideRoles(String authorizationKey) {
                        return null;
                    }
                };
            }
            setHttpHandler(httpServer, jsonNode.path("http-handlers"), roleProvidable);
//            JsonNode assetHandlers = jsonNode.path("asset-handlers");
//            for(int i = 0; i < assetHandlers.size(); i++) {
//                JsonNode assetHandler = assetHandlers.get(i);
//                JsonNode paths = assetHandler.get("paths");
//                Set<String> pathList = new HashSet<>();
//                for (int j = 0; j < paths.size(); j++) {
//                    pathList.add(paths.get(j).textValue());
//                }
//                StaticHttpHandler handler = new StaticHttpHandler(pathList);
//                JsonNode uris = assetHandler.get("uris");
//                List<String> uriList = new ArrayList<>();
//                for (int j = 0; j < uris.size(); j++) {
//                    uriList.add(uris.get(j).textValue());
//                }
//                httpServer.addHttpHandler(handler, uriList.toArray(new String[uriList.size()]));
//            }
            for(Listener listener : server.getListeners()) {
                NetworkListener networkListener = new NetworkListener(
                        listener.getName(), listener.getHost(), listener.getPort());
                setSSLConfig(networkListener, listener.getSslConfig());
                httpServer.addNetworkListener(networkListener);
            }
            httpServerList.add(httpServer);

            PianaSecure.createInstance(server.getName(), server.getSession());
        }

        for(Redirector redirector : redirectorList) {
            NetworkListener networkListener = new NetworkListener(
                    redirector.getName(), redirector.getHost(), redirector.getPort());
            setSSLConfig(networkListener, redirector.getSslConfig());
            GrizzlyHttpServer httpServer = new GrizzlyHttpServer(redirector.getName());
            httpServer.addNetworkListener(networkListener);

            PianaHttpHandlerRedirector redirectorHandler = null;
            for(Server server : serverList) {
                if(server.getName().equals(redirector.getToSerevr())) {
                    for(Listener listener : server.getListeners()) {
                        if(listener.getName().equals(redirector.getToListener())) {
                            redirectorHandler = new PianaHttpHandlerRedirector(
                                    listener.getHost(), listener.getPort(), listener.getSslConfig() != null);
                        }
                    }
                }
            }

            if(redirectorHandler != null)
                httpServer.addHttpHandler(redirectorHandler);
            httpServerList.add(httpServer);
        }
    }

    protected void startService() throws Exception {
        for(GrizzlyHttpServer httpServer : httpServerList) {
            httpServer.startService();
        }
        System.out.println("start");
    }

    protected void stopService() throws Exception {
        List<GrizzlyFuture<HttpServer>> list = new ArrayList<>();
        for(GrizzlyHttpServer httpServer : httpServerList) {
            list.add(httpServer.stopService());
        }

        for (GrizzlyFuture<HttpServer> future : list) {
            try {
                future.get();
            } catch(InterruptedException | ExecutionException e) {
                logger.error("Error while shutting down server.", e);
            }
        }
        logger.info("All server's stopped.");
    }

    @Override
    public void setConfiguration(Element element)
            throws ConfigurationException {
        System.out.println("config");
        fillServers(element.getChild("servers"));
        fillRedirectors(element.getChild("redirectors"));
    }

    private void fillServers(Element serversElement) {
        List<Element> list = serversElement.getChildren("server");

        for(Element serverElement : list) {
            serverList.add(new Server(
                    serverElement.getChild("name").getValue(),
                    fillListeners(serverElement.getChild("listeners")),
                    fillSession(serverElement.getChild("session")),
                    serverElement.getChild("piana-role-providable").getValue()));
        }
    }

    private List<Listener> fillListeners(Element listenersElement) {
        List<Element> list = listenersElement.getChildren("listener");
        List<Listener> listenerList = new ArrayList<>();
        for (Element listenerElement : list) {
            listenerList.add(new Listener(
                    listenerElement.getChild("name").getValue(),
                    listenerElement.getChild("host").getValue(),
                    listenerElement.getChild("port").getValue(),
                    fillSSLConfig(listenerElement.getChild("ssl-config"))));
        }
        return listenerList;
    }

    private Session fillSession(Element sessionElement) {
        Session session = new Session(
                sessionElement.getChild("name").getValue(),
                Integer.parseInt(sessionElement.getChild("cache-size").getValue()),
                Integer.parseInt(sessionElement.getChild("expired-second").getValue()));
        return session;
    }

    private SSLConfig fillSSLConfig(Element sslConfigElement) {
        if(sslConfigElement != null)
            return new SSLConfig(
                    sslConfigElement.getChild("key-store-path").getValue(),
                    sslConfigElement.getChild("key-store-pass").getValue());
        return null;
    }

    private List<Redirector> fillRedirectors(Element redirectorsElement) {
        List<Element> list = redirectorsElement.getChildren("redirector");
        for (Element redirectorElement : list) {
            redirectorList.add(new Redirector(
                    redirectorElement.getChild("name").getValue(),
                    redirectorElement.getChild("host").getValue(),
                    redirectorElement.getChild("port").getValue(),
                    fillSSLConfig(redirectorElement.getChild("ssl-config")),
                    redirectorElement.getChild("redirec-to").getChild("server").getValue(),
                    redirectorElement.getChild("redirec-to").getChild("listener").getValue()
            ));
        }
        return redirectorList;
    }

    protected void setSSLConfig(NetworkListener networkListener, SSLConfig sslConfig)
            throws IOException {
        if(sslConfig != null) {
            byte[] bytes = Files.readAllBytes(
                    Paths.get("./ssl/".concat(sslConfig.getKeyStorePath())));
            SSLContextConfigurator sslCon = new SSLContextConfigurator();
//            InputStream is = WebPortalModule.class.getResourceAsStream(
//                    sslConfig.getKeyStorePath());
//            byte[] bytes = IOUtils.toByteArray(is);
            sslCon.setKeyStoreBytes(bytes); // contains web keypair
            sslCon.setKeyStorePass(sslConfig.getKeyStorePass());
            networkListener.setSecure(true);
            networkListener.setSSLEngineConfig(new SSLEngineConfigurator(sslCon)
                    .setClientMode(false)
                    .setNeedClientAuth(false));
        }
    }

    protected JsonNode readMapping(String mappingName)
            throws IOException {
        //read json file data to String
        byte[] jsonData = Files.readAllBytes(Paths.get("./mapping/".concat(mappingName)));

        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        //read JSON like DOM Parser
        JsonNode rootNode = objectMapper.readTree(jsonData);
        return rootNode;
    }

    protected void setAssetHandler(GrizzlyHttpServer httpServer, JsonNode assetHandlersNode) {
        for(int i = 0; i < assetHandlersNode.size(); i++) {
            JsonNode assetHandler = assetHandlersNode.get(i);
            JsonNode paths = assetHandler.get("paths");
            Set<String> pathList = new HashSet<>();
            for (int j = 0; j < paths.size(); j++) {
                pathList.add(paths.get(j).textValue());
            }
            StaticHttpHandler handler = new PianaHttpHandlerStatic(pathList);
            if(handler instanceof PianaHttpHandlerNameInjectable) {
                ((PianaHttpHandlerNameInjectable)handler).injectServerName(httpServer.getName());
            }
            JsonNode uris = assetHandler.get("uris");
            List<String> uriList = new ArrayList<>();
            for (int j = 0; j < uris.size(); j++) {
                uriList.add(uris.get(j).textValue());
            }
            httpServer.addHttpHandler(handler, uriList.toArray(new String[uriList.size()]));
        }
    }

    protected void setHttpHandler(GrizzlyHttpServer httpServer, JsonNode httpHandlersNode,
                                  PianaRoleProvidable roleProvidable)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        for(int i = 0; i < httpHandlersNode.size(); i++) {
            JsonNode httpHandlerNode = httpHandlersNode.get(i);
            JsonNode uris = httpHandlerNode.get("uris");
            List<String> uriList = new ArrayList<>();
            for (int j = 0; j < uris.size(); j++) {
                uriList.add(uris.get(j).textValue());
            }
            JsonNode handler = httpHandlerNode.get("handler");
            PianaHttpHandlerBase httpHandler = (PianaHttpHandlerBase) Class.forName(handler.asText()).newInstance();
            httpHandler.setRoleProvidable(roleProvidable);
            if(httpHandler instanceof PianaHttpHandlerInjectable) {
                JsonNode data = httpHandlerNode.get("data");
                Map dataMap = null;
                if(data != null && data.isObject()) {
                    ObjectMapper mapper = new ObjectMapper();
                    dataMap = mapper.convertValue(data, Map.class);
                } else {
                    dataMap = new LinkedHashMap();
                }
                ((PianaHttpHandlerInjectable)httpHandler).injectData(dataMap);

                JsonNode headersNode = httpHandlerNode.get("headers");
                Map headersMap = null;
                if(headersNode != null && headersNode.isObject()) {
                    ObjectMapper mapper = new ObjectMapper();
                    headersMap = mapper.convertValue(headersNode, Map.class);
                } else {
                    headersMap = new LinkedHashMap();
                }
                ((PianaHttpHandlerInjectable)httpHandler).injectHeaders(headersMap);

                JsonNode rolesNode = httpHandlerNode.get("roles");
                List<String> roleArray = new ArrayList<>();
                if(rolesNode != null) {
                    for(int j = 0; j < rolesNode.size(); j++) {
                        roleArray.add(rolesNode.get(j).textValue());
                    }
                }
                ((PianaHttpHandlerInjectable)httpHandler).injectRoles(roleArray);
            }
            if(httpHandler instanceof PianaHttpHandlerInitializable) {
                ((PianaHttpHandlerInitializable)httpHandler).initialize();
            }
            if(httpHandler instanceof PianaHttpHandlerNameInjectable) {
                ((PianaHttpHandlerNameInjectable)httpHandler).injectServerName(httpServer.getName());
            }
            httpServer.addHttpHandler(httpHandler, uriList.toArray(new String[uriList.size()]));
        }
    }
}
