package application;

import filesystem.FileIndexer;
import report.JsonReportStrategy;
import report.ReportGenerationStrategy;
import report.TxtReportStrategy;

import java.nio.file.Paths;
import java.util.Scanner;

public class RunIndexer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose report format (txt/json): ");
        String format = scanner.nextLine().trim().toLowerCase();

        ReportGenerationStrategy strategy;

        if (format.equals("json")) {
            strategy = new JsonReportStrategy();
        } else {
            strategy = new TxtReportStrategy();
        }

        FileIndexer indexer = new FileIndexer(strategy);
        indexer.indexFolder(Paths.get("C:\\Users\\Mara\\Documents\\UTCN AN 3 SEM 2"));
        //indexer.indexFolder(Paths.get("C:\\Users\\Mara\\Documents\\UTCN AN 3 SEM 2\\SOFTWARE DESIGN\\Laboratory\\examples"));
    }
}
