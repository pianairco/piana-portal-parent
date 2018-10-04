package ir.piana.dev.portal.web.handler;

import java.util.List;
import java.util.Map;

/**
 * @author Mohammad Rahmati, 10/2/2018
 */
public interface PianaHttpHandlerInjectable {
    void injectData(Map<String, Object> dataMap);
    void injectHeaders(Map<String, String> headers);
    void injectRoles(List<String> roleArray);
}
