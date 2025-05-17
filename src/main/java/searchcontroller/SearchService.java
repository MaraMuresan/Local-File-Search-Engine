package searchcontroller;

import database.CachedQueryExecutor;
import filesystem.IndexReader;

import java.util.List;
import java.util.Map;

public class SearchService {
    private final CachedQueryExecutor dbExecutor;
    private final IndexReader fileIndexer;

    public SearchService(CachedQueryExecutor dbExecutor, IndexReader fileIndexer) {
        this.dbExecutor = dbExecutor;
        this.fileIndexer = fileIndexer;
    }

    public Map<String, List<String[]>> advancedSearch(Map<String, String> qualifiers, Map<String, Integer> frequencyMap) {
        return dbExecutor.searchWithQualifiers(qualifiers, frequencyMap);
    }

}
