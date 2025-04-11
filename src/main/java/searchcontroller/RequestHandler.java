package searchcontroller;

import java.util.HashMap;
import java.util.Map;

public class RequestHandler {

    public boolean isValidQuery(String query) {
        return query != null && !query.isBlank();
    }

    public Map<String, String> parseQuery(String query) {
        Map<String, String> qualifiers = new HashMap<>();

        String[] parts = query.trim().split("\\s+");
        for (String part : parts) {
            if (part.contains(":")) {
                String[] split = part.split(":", 2);
                String key = split[0].toLowerCase();
                String value = split[1].replace("\\", "/");
                qualifiers.merge(key, value, (oldVal, newVal) -> oldVal + " AND " + newVal);
            }
        }
        return qualifiers;
    }

}
