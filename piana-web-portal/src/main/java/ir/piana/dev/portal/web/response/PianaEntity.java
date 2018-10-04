package ir.piana.dev.portal.web.response;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Mohammad Rahmati, 10/3/2018
 */
public class PianaEntity {
    private Map map = new LinkedHashMap<>();

    private PianaEntity() {
    }

    public static PianaEntity create() {
        return new PianaEntity();
    }

    public PianaEntity add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

    public Map build() {
        return map;
    }
}
