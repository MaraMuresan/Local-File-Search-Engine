package report;

import java.util.List;

public interface ReportGenerationStrategy {
    void generateReport(List<String> indexedFiles, List<String> skippedFiles, List<String> failedFiles);
}