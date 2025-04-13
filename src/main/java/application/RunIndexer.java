package application;

import filesystem.FileIndexer;
import ranking.*;
import report.JsonReportStrategy;
import report.ReportGenerationStrategy;
import report.ReportStrategyFactory;
import report.TxtReportStrategy;

import java.nio.file.Paths;
import java.util.Scanner;

public class RunIndexer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose report format (txt/json): ");
        String format = scanner.nextLine().trim().toLowerCase();

        ReportGenerationStrategy strategy = ReportStrategyFactory.createStrategy(format);

        System.out.println("Choose ranking boost strategy (size / recency / both): ");
        String boostType = scanner.nextLine().trim().toLowerCase();

        RankingBoostStrategy boostStrategy = RankingBoostStrategyFactory.createStrategy(boostType);

        FileIndexer indexer = new FileIndexer(strategy, boostStrategy);
        indexer.indexFolder(Paths.get("C:\\Users\\Mara\\Documents\\UTCN AN 3 SEM 2"));
        //indexer.indexFolder(Paths.get("C:\\Users\\Mara\\Documents\\UTCN AN 3 SEM 2\\SOFTWARE DESIGN\\Laboratory\\examples"));
    }
}
