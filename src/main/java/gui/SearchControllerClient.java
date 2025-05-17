package gui;

import observer.SearchHistoryLogger;
import observer.SearchObserver;
import searchcontroller.CorrectionStrategy;
import searchcontroller.RequestHandler;
import searchcontroller.SearchService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchControllerClient {
    private final SearchService searchService;
    private final ResultDisplay resultDisplay;
    private final StatusBox statusBox;
    private final List<SearchObserver> observers = new ArrayList<>();
    private final SearchHistoryLogger historyLogger;
    private final WidgetPanel widgetPanel;
    private final CorrectionStrategy spellingCorrector;

    public SearchControllerClient(SearchService service, ResultDisplay display, StatusBox status, SearchHistoryLogger historyLogger, WidgetPanel widgetPanel, CorrectionStrategy spellingCorrector) {
        this.searchService = service;
        this.resultDisplay = display;
        this.statusBox = status;
        this.historyLogger = historyLogger;
        this.widgetPanel = widgetPanel;
        this.spellingCorrector = spellingCorrector;
        addObserver(historyLogger);
    }

    public void sendSearchQuery(String query) {
        RequestHandler handler = new RequestHandler();

        if (handler.isValidQuery(query)) {

            String[] terms = query.trim().split("\\s+");
            StringBuilder corrected = new StringBuilder();

            for (String term : terms) {
                if (term.contains(":")) {
                    String[] parts = term.split(":", 2);
                    String prefix = spellingCorrector.correct(parts[0]);
                    String raw = spellingCorrector.correct(parts[1]);
                    corrected.append(prefix).append(":").append(raw).append(" ");
                } else {
                    corrected.append(spellingCorrector.correct(term)).append(" ");
                }
            }

            String originalQuery = query.trim();
            query = corrected.toString().trim();

            if (!query.equalsIgnoreCase(originalQuery)) {
                int choice = JOptionPane.showConfirmDialog(
                        null,
                        "Did you mean:\n" + query + "?",
                        "Spelling Suggestion",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.NO_OPTION) {
                    query = originalQuery;
                }
            }

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

            widgetPanel.showWidgetsForQuery(query);

        } else {
            statusBox.setStatus("Invalid query.");
        }
    }

    public void addObserver(SearchObserver observer) {
        observers.add(observer);
    }

}
