package database;

import java.util.*;

public class CachedQueryExecutor {
    private final QueryExecutor realExecutor = new QueryExecutor();
    private final Map<String, Map<String, List<String[]>>> cache = new HashMap<>();

    public Map<String, List<String[]>> searchWithQualifiers(Map<String, String> qualifiers, Map<String, Integer> frequencyMap) {
        String cacheKey = buildCacheKey(qualifiers);

        if (cache.containsKey(cacheKey)) {
            System.out.println("Cache hit for: " + cacheKey);
            return cache.get(cacheKey);
        }

        System.out.println("Cache miss. Querying database for: " + cacheKey);
        Map<String, List<String[]>> result = realExecutor.searchWithQualifiers(qualifiers, frequencyMap);
        cache.put(cacheKey, result);
        return result;
    }

    private String buildCacheKey(Map<String, String> qualifiers) {
        return qualifiers.toString().toLowerCase();
    }
}
