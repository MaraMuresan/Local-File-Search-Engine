package gui;

import observer.SearchHistoryLogger;
import observer.SearchObserver;
import searchcontroller.RequestHandler;
import searchcontroller.SearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchControllerClient {
    private final SearchService searchService;
    private final ResultDisplay resultDisplay;
    private final StatusBox statusBox;
    private final List<SearchObserver> observers = new ArrayList<>();
    private final SearchHistoryLogger historyLogger;

    public SearchControllerClient(SearchService service, ResultDisplay display, StatusBox status, SearchHistoryLogger historyLogger) {
        this.searchService = service;
        this.resultDisplay = display;
        this.statusBox = status;
        this.historyLogger = historyLogger;
        addObserver(historyLogger);
    }

    public void sendSearchQuery(String query) {
        RequestHandler handler = new RequestHandler();

        if (handler.isValidQuery(query)) {

            for (SearchObserver observer : observers) {
                observer.onSearch(query);
            }

            statusBox.setHistory(historyLogger.getLastSearches(5));

            Map<String, Integer> frequencyMap = historyLogger.getQueryFrequencies();

            Map<String, String> parsed = handler.parseQuery(query);
            Map<String, List<String[]>> resultMap = searchService.advancedSearch(parsed, frequencyMap);

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

    public void addObserver(SearchObserver observer) {
        observers.add(observer);
    }

}
