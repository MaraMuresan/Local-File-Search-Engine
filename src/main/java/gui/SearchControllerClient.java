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
        String validQuery = handler.validateQuery(query);

        if (validQuery != null) {
            Map<String, List<String[]>> resultMap = searchService.search(validQuery);
            List<String[]> nameMatches = resultMap.get("name");
            List<String[]> contentMatches = resultMap.get("content");

            resultDisplay.updateResults(nameMatches, contentMatches, validQuery);

            int total = nameMatches.size() + contentMatches.size();
            statusBox.setStatus("Found " + total + " results.");
        } else {
            statusBox.setStatus("Invalid query.");
        }
    }
}
