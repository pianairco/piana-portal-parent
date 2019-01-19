package ir.piana.dev.portal.web.handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import ir.piana.dev.portal.web.secure.PianaAuthorizer;
import ir.piana.dev.portal.web.secure.PianaRoleProvidable;
import ir.piana.dev.portal.web.secure.PianaSecure;
import ir.piana.dev.portal.web.secure.PianaSession;
import org.glassfish.grizzly.http.Cookie;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.Header;
import org.glassfish.grizzly.http.util.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mohammad Rahmati, 9/30/2018
 */
public class PianaHttpHandlerBase extends HttpHandler
        implements PianaHttpHandler,
        PianaHttpHandlerInjectable,
        PianaHttpHandlerInitializable,
        PianaHttpHandlerNameInjectable {
    protected String serverName;
    protected PianaRoleProvidable roleProvidable;
    protected Map<String, Object> dataMap = null;
    protected Map<String, String> headersMap = null;
    protected List<String> roleArray = null;

    @Override
    public final void service(Request request, Response response)
            throws Exception {
        if(!authorize(request, response)) {
         response.setStatus(HttpStatus.UNAUTHORIZED_401);
         return;
        }

        if (request.getMethod() == Method.GET) {
            this.get(request, response);
        } else if(request.getMethod() == Method.POST) {
            this.post(request, response);
        } else if(request.getMethod() == Method.PUT) {
            this.put(request, response);
        } else if(request.getMethod() == Method.DELETE) {
            this.delete(request, response);
        } else if(request.getMethod() == Method.OPTIONS) {
            this.options(request, response);
        } else if(request.getMethod() == Method.HEAD) {
            this.head(request, response);
        } else if(request.getMethod() == Method.PATCH) {
            this.patch(request, response);
        }

        for (String key : headersMap.keySet()) {
           response.addHeader(key, headersMap.get(key));
        }
    }

    private final boolean authorize(Request request, Response response) {
        if(roleArray.size() == 0)
            return true;
        else if(roleArray.size() > 0 && roleProvidable == null)
            return false;
        else {
            PianaSession pianaSession = PianaSecure.getSessionManager(serverName)
                    .retrieveSessionIfExist(request, response);
            if(pianaSession == null || pianaSession.getExistance() == null)
                return false;
            else {
                Object existance = pianaSession.getExistance();
                List<String> providedRoles = roleProvidable.provideRoles((String) existance);
                if(providedRoles.size() > 0) {
                    for (String role : roleArray) {
                        for(String providedRole : providedRoles) {
                            if(providedRole.equalsIgnoreCase(role)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    protected final String setAuthorize(Request request, Response response, String authorizationKey) {
        return PianaAuthorizer.authorize(serverName, request, response, authorizationKey);
    }

    protected final void clearAuthorize(Request request, Response response) {
        PianaAuthorizer.clear(serverName, request, response);
    }

    public void toJsonString(Response response, Object obj) {
        try {
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            ObjectMapper objectMapper = new ObjectMapper();
            String data = objectMapper.writeValueAsString(obj);
            response.getWriter().write(data);
            response.setStatus(HttpStatus.OK_200);
        } catch (IOException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
        }
    }

    public Map fromJsonBody(Request request) {
        Map map = fromJsonBody(request, Map.class);
        return map != null ? map : new LinkedHashMap();
    }

    public <T> T fromJsonBody(Request request, Class type) {
        try {
            if (request.getMethod() == Method.POST || request.getMethod() == Method.PUT) {
                if (request.getHeader("Content-Type").startsWith("application/json")) {
                    ObjectMapper mapper = new ObjectMapper();
                    ByteSource byteSource = new ByteSource() {
                        @Override
                        public InputStream openStream() throws IOException {
                            return request.getInputStream();
                        }
                    };
                    String requestBody = byteSource.asCharSource(Charsets.UTF_8).read();
                    return (T)mapper.readValue(requestBody, type);
                }
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public final void injectServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public void injectData(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    @Override
    public void injectHeaders(Map<String, String> headers) {
        this.headersMap = headers;
    }

    @Override
    public void injectRoles(List<String> roleArray) {
        this.roleArray = roleArray;
    }

    @Override
    public void initialize() {
        System.out.println("Handler initialize...");
    }

    public void setRoleProvidable(PianaRoleProvidable roleProvidable) {
        this.roleProvidable = roleProvidable;
    }
}
