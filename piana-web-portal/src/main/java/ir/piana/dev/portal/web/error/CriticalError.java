package ir.piana.dev.portal.web.error;

/**
 * @author Mohammad Rahmati, 9/29/2018
 */
public class CriticalError extends Exception {
    public CriticalError(String message) {
        super(message);
    }
}
