package searchcontroller;

import database.QueryExecutor;
import filesystem.IndexReader;

import java.util.List;
import java.util.Map;

public class SearchService {
    private final QueryExecutor dbExecutor;
    private final IndexReader fileIndexer;

    public SearchService(QueryExecutor dbExecutor, IndexReader fileIndexer) {
        this.dbExecutor = dbExecutor;
        this.fileIndexer = fileIndexer;
    }

    public Map<String, List<String[]>> advancedSearch(Map<String, String> qualifiers) {
        return dbExecutor.searchWithQualifiers(qualifiers);
    }

}
