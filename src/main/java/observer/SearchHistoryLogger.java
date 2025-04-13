package observer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchHistoryLogger implements SearchObserver {
    private static final String HISTORY_FILE = "search_history.txt";

    @Override
    public void onSearch(String query) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE, true))) {
            String[] parts = query.trim().toLowerCase().split("\\s+");
            for (String part : parts) {
                if (part.contains(":")) {
                    writer.write(part);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to write search to history: " + e.getMessage());
        }
    }


    public List<String> getLastSearches(int count) {
        List<String> history = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HISTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                history.add(line);
            }
        } catch (IOException e) {
            System.err.println("Failed to read search history: " + e.getMessage());
        }

        int start = Math.max(0, history.size() - count);
        return history.subList(start, history.size());
    }

    public Map<String, Integer> getQueryFrequencies() {
        Map<String, Integer> frequencies = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HISTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                frequencies.put(line, frequencies.getOrDefault(line, 0) + 1);
            }
        } catch (IOException e) {
            System.err.println("Failed to read search history: " + e.getMessage());
        }
        return frequencies;
    }

}
