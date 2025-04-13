package application;

import filesystem.FileIndexer;
import ranking.CombinedBoostStrategy;
import ranking.RankingBoostStrategy;
import ranking.RecencyBoostStrategy;
import ranking.SizeBoostStrategy;
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

        System.out.println("Choose ranking boost strategy (size / recency / both): ");
        String boostType = scanner.nextLine().trim().toLowerCase();

        RankingBoostStrategy boostStrategy = switch (boostType) {
            case "size" -> new SizeBoostStrategy();
            case "recency" -> new RecencyBoostStrategy();
            case "both" -> new CombinedBoostStrategy();
            default -> new CombinedBoostStrategy();
        };

        FileIndexer indexer = new FileIndexer(strategy, boostStrategy);
        indexer.indexFolder(Paths.get("C:\\Users\\Mara\\Documents\\UTCN AN 3 SEM 2"));
        //indexer.indexFolder(Paths.get("C:\\Users\\Mara\\Documents\\UTCN AN 3 SEM 2\\SOFTWARE DESIGN\\Laboratory\\examples"));
    }
}
