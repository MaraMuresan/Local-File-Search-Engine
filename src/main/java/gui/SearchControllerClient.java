package gui;

import searchcontroller.RequestHandler;
import searchcontroller.SearchService;

import java.util.List;
import java.util.Map;

public class SearchControllerClient {
    private final SearchService searchService;
    private final ResultDisplay resultDisplay;
    private final StatusBox statusBox;

    public SearchControllerClient(SearchService service, ResultDisplay display, StatusBox status) {
        this.searchService = service;
        this.resultDisplay = display;
        this.statusBox = status;
    }

    public void sendSearchQuery(String query) {
        RequestHandler handler = new RequestHandler();

        if (handler.isValidQuery(query)) {
            Map<String, String> parsed = handler.parseQuery(query);
            Map<String, List<String[]>> resultMap = searchService.advancedSearch(parsed);

            List<String[]> pathMatches = resultMap.get("path");
            List<String[]> contentMatches = resultMap.get("content");

            String highlightQuery = parsed.getOrDefault("content", "");

            resultDisplay.updateResults(pathMatches, contentMatches, highlightQuery);

            int total = pathMatches.size() + contentMatches.size();
            statusBox.setStatus("Found " + total + " results.");
        } else {
            statusBox.setStatus("Invalid query.");
        }
    }

}
