package ir.piana.dev.portal.web.secure;

import java.util.List;

/**
 * @author Mohammad Rahmati, 10/4/2018
 */
public interface PianaRoleProvidable {
    List<String> provideRoles(String authorizationKey);
}
