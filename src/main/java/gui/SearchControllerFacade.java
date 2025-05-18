package gui;

import database.CachedQueryExecutor;
import observer.SearchHistoryLogger;
import observer.SearchObserver;
import searchcontroller.CorrectionStrategy;
import searchcontroller.RequestHandler;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchControllerFacade{
    private final CachedQueryExecutor dbExecutor;
    private final ResultDisplay resultDisplay;
    private final StatusBox statusBox;
    private final List<SearchObserver> observers = new ArrayList<>();
    private final SearchHistoryLogger historyLogger;
    private final WidgetPanel widgetPanel;
    private final CorrectionStrategy spellingCorrector;

    public SearchControllerFacade(CachedQueryExecutor dbExecutor, ResultDisplay display, StatusBox status, SearchHistoryLogger historyLogger, WidgetPanel widgetPanel, CorrectionStrategy spellingCorrector) {
        this.dbExecutor = dbExecutor;
        this.resultDisplay = display;
        this.statusBox = status;
        this.historyLogger = historyLogger;
        this.widgetPanel = widgetPanel;
        this.spellingCorrector = spellingCorrector;
        addObserver(historyLogger);
    }

    public void handleSearch(String query) {
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
            Map<String, List<String[]>> resultMap = dbExecutor.searchWithQualifiers(parsed, frequencyMap);

            List<String[]> pathMatches = resultMap.get("path");
            List<String[]> contentMatches = resultMap.get("content");

            String highlightQuery = parsed.getOrDefault("content", "");

            resultDisplay.updateResults(pathMatches, contentMatches, highlightQuery);

            int total = pathMatches.size() + contentMatches.size();

            List<String[]> allResults = new ArrayList<>();
            allResults.addAll(pathMatches);
            allResults.addAll(contentMatches);

            Map<String, Integer> fileTypes = new HashMap<>();
            Map<String, Integer> months = new HashMap<>();
            Map<String, Integer> sizeBuckets = new HashMap<>();

            for (String[] row : allResults) {
                String ext = row.length > 3 ? row[3].toLowerCase() : "unknown";
                fileTypes.put(ext, fileTypes.getOrDefault(ext, 0) + 1);

                if (row.length > 4) {
                    String timestamp = row[4];
                    if (timestamp.length() >= 7) {
                        String monthPart = timestamp.substring(5, 7);
                        String monthName = getMonthName(monthPart);
                        months.put(monthName, months.getOrDefault(monthName, 0) + 1);
                    }
                }

                long sizeBytes = 0;
                if (row.length > 5) {
                    try {
                        sizeBytes = Long.parseLong(row[5]);
                    } catch (Exception ignored) {}
                    String bucket = getBucketSize(sizeBytes);
                    sizeBuckets.put(bucket, sizeBuckets.getOrDefault(bucket, 0) + 1);
                }
            }

            String typeSummary = fileTypes.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .map(e -> e.getKey().toUpperCase() + " (" + e.getValue() + ")")
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("None");

            String monthSummary = months.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .map(e -> e.getKey() + " (" + e.getValue() + ")")
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("None");

            String sizeSummary = sizeBuckets.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .map(e -> e.getKey() + " (" + e.getValue() + ")")
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("None");

            String statusText = "Found " + total + " results\n" +
                    "File Type: " + typeSummary + "\n" +
                    "Modified Month: " + monthSummary + "\n" +
                    "File Size: " + sizeSummary;

            statusBox.setStatus(statusText);

            List<JPanel> widgets = WidgetFactory.getContextWidgets(allResults);
            widgetPanel.showWidgets(query, widgets);


        } else {
            statusBox.setStatus("Invalid query.");
        }
    }

    public void addObserver(SearchObserver observer) {
        observers.add(observer);
    }

    private String getMonthName(String monthNum) {
        return switch (monthNum) {
            case "01" -> "January";
            case "02" -> "February";
            case "03" -> "March";
            case "04" -> "April";
            case "05" -> "May";
            case "06" -> "June";
            case "07" -> "July";
            case "08" -> "August";
            case "09" -> "September";
            case "10" -> "October";
            case "11" -> "November";
            case "12" -> "December";
            default -> "Unknown";
        };
    }

    private String getBucketSize(long sizeBytes) {
        if (sizeBytes < 100_000) { //100 KB
            return "Small";
        } else if (sizeBytes <= 1_000_000) { //1 MB
           return "Medium";
        } else {
            return "Large";
        }
    }

}
