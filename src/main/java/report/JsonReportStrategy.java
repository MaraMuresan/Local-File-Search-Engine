package report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class JsonReportStrategy implements ReportGenerationStrategy {

    @Override
    public void generateReport(List<String> indexedFiles, List<String> skippedFiles, List<String> failedFiles) {
        Path reportPath = Paths.get("index_report.json");

        Map<String, Object> reportData = new LinkedHashMap<>();
        reportData.put("indexedFiles", indexedFiles);
        reportData.put("skippedFiles", skippedFiles);
        reportData.put("failedFiles", failedFiles);
        reportData.put("generatedOn", new Date().toString());

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

        try {
            writer.writeValue(Files.newBufferedWriter(reportPath), reportData);
            System.out.println("Indexing report saved to: " + reportPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error writing JSON report: " + e.getMessage());
        }
    }
}
