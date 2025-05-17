package application;

import database.CachedQueryExecutor;
import filesystem.IndexReader;
import gui.*;
import gui.SearchControllerFacade;
import searchcontroller.CorrectionStrategy;
import searchcontroller.SearchService;
import observer.SearchHistoryLogger;
import searchcontroller.SpellingCorrectorV1;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CachedQueryExecutor executor = new CachedQueryExecutor();
            IndexReader indexReader = new IndexReader();
            SearchService service = new SearchService(executor, indexReader);

            ResultDisplay display = new ResultDisplay();
            StatusBox status = new StatusBox();
            SearchHistoryLogger historyLogger = new SearchHistoryLogger();
            WidgetPanel widgetPanel = new WidgetPanel();
            CorrectionStrategy corrector;
            try {
                corrector = new SpellingCorrectorV1(new File("search_history.txt"));
            } catch (IOException e) {
                e.printStackTrace();
                corrector = word -> word;
            }

            SearchControllerFacade facade = new SearchControllerFacade(executor, display, status, historyLogger, widgetPanel, corrector);

            SearchControllerClient client = new SearchControllerClient(facade);

            SearchInput input = new SearchInput(client);

            status.setHistory(historyLogger.getLastSearches(3));

            JFrame frame = new JFrame("Local File Search Engine");
            frame.setSize(900, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(input, "North");
            frame.add(display, "Center");
            frame.add(status, "South");
            frame.add(widgetPanel, "East");
            frame.pack();
            frame.setVisible(true);
        });
    }
}
