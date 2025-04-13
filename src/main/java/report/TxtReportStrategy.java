package report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TxtReportStrategy implements ReportGenerationStrategy {

    @Override
    public void generateReport(List<String> indexedFiles, List<String> skippedFiles, List<String> failedFiles) {
        Path reportPath = Paths.get("index_report.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(reportPath)) {
            writer.write("Indexing Report:\n\n");

            writer.write("Indexed Files (" + indexedFiles.size() + "):\n");
            for (String file : indexedFiles) {
                writer.write(" - " + file + "\n");
            }

            writer.write("\nSkipped Files (" + skippedFiles.size() + "):\n");
            for (String file : skippedFiles) {
                writer.write(" - " + file + "\n");
            }

            writer.write("\nFailed Files (" + failedFiles.size() + "):\n");
            for (String file : failedFiles) {
                writer.write(" - " + file + "\n");
            }

            writer.write("\nReport generated on: " + new java.util.Date() + "\n");

            System.out.println("Indexing report saved to: " + reportPath.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error writing TXT report: " + e.getMessage());
        }
    }
}
