package application;

import database.QueryExecutor;
import filesystem.IndexReader;
import gui.*;
import searchcontroller.SearchService;
import observer.SearchHistoryLogger;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QueryExecutor executor = new QueryExecutor();
            IndexReader indexReader = new IndexReader();
            SearchService service = new SearchService(executor, indexReader);

            ResultDisplay display = new ResultDisplay();
            StatusBox status = new StatusBox();
            SearchHistoryLogger historyLogger = new SearchHistoryLogger();
            SearchControllerClient client = new SearchControllerClient(service, display, status, historyLogger);
            SearchInput input = new SearchInput(client);

            status.setHistory(historyLogger.getLastSearches(3));

            JFrame frame = new JFrame("Local File Search Engine");
            frame.setSize(900, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(input, "North");
            frame.add(display, "Center");
            frame.add(status, "South");
            frame.pack();
            frame.setVisible(true);
        });
    }
}
