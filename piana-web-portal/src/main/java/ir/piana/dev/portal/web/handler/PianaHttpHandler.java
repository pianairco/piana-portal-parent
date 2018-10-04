package ir.piana.dev.portal.web.handler;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.HttpStatus;

/**
 * @author Mohammad Rahmati, 9/30/2018
 */
public interface PianaHttpHandler {
    default void get(Request request, Response response) {
        response.setStatus(HttpStatus.OK_200);
    }

    default void post(Request request, Response response) {
        response.setStatus(HttpStatus.OK_200);
    }

    default void put(Request request, Response response) {
        response.setStatus(HttpStatus.OK_200);
    }

    default void delete(Request request, Response response) {
        response.setStatus(HttpStatus.OK_200);
    }

    default void options(Request request, Response response) {
        response.setHeader(
                "Access-Control-Allow-Headers",
                request.getHeader("Access-Control-Request-Headers"));
        response.setStatus(HttpStatus.OK_200);
    }

    default void head(Request request, Response response) {
        response.setStatus(HttpStatus.OK_200);
    }

    default void patch(Request request, Response response) {
        response.setStatus(HttpStatus.OK_200);
    }
}
