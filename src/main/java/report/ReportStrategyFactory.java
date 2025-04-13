package report;

public class ReportStrategyFactory {
    public static ReportGenerationStrategy createStrategy(String format) {
        return switch (format.toLowerCase()) {
            case "json" -> new JsonReportStrategy();
            case "txt" -> new TxtReportStrategy();
            default -> new TxtReportStrategy();
        };
    }
}