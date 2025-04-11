package filesystem;

import org.apache.tika.Tika;

import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;

public class FileIndexer {

    private static final Tika tika = new Tika();

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/LocalFileSearchEngine";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "postgres";

    private final List<String> indexedFiles = new ArrayList<>();
    private final List<String> skippedFiles = new ArrayList<>();
    private final List<String> failedFiles = new ArrayList<>();

    public void indexFolder(Path folderPath) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            try (Statement cleanupStmt = conn.createStatement()) {
                cleanupStmt.executeUpdate("DELETE FROM file_index");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Files.walk(folderPath)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            String mimeType = tika.detect(file);
                            if (!mimeType.startsWith("text")) {
                                skippedFiles.add(file.toString());
                                System.out.println("Skipping non-text file: " + file);
                                return;
                            }

                            String content = Files.readString(file);
                            String filePath = file.toString().replace("\\", "/");
                            String extension = getFileExtension(filePath);
                            String tags = "";
                            Timestamp timestamp = new Timestamp(new Date().getTime());
                            long size = Files.size(file); //bytes

                            PreparedStatement stmt = conn.prepareStatement("""
                                INSERT INTO file_index (file_path, content, extension, tags, timestamp, size, index_file_name, index_content)
                                VALUES (?, ?, ?, ?, ?, ?, to_tsvector('english', ?), to_tsvector('english', ?))
                                ON CONFLICT (file_path) DO UPDATE 
                                SET content = EXCLUDED.content,
                                extension = EXCLUDED.extension,
                                tags = EXCLUDED.tags,
                                timestamp = EXCLUDED.timestamp,
                                size = EXCLUDED.size,
                                index_file_name = EXCLUDED.index_file_name,
                                index_content = EXCLUDED.index_content
                            """);

                            stmt.setString(1, filePath);
                            stmt.setString(2, content);
                            stmt.setString(3, extension);
                            stmt.setString(4, tags);
                            stmt.setTimestamp(5, timestamp);
                            stmt.setLong(6, size);
                            stmt.setString(7, filePath);
                            stmt.setString(8, content);

                            stmt.executeUpdate();

                            indexedFiles.add(file.toString());
                            System.out.println("Indexed: " + filePath);

                        } catch (Exception e) {
                            failedFiles.add(file.toString() + " → " + e.getMessage());
                            System.err.println("Error indexing " + file + ": " + e.getMessage());
                        }
                    });
            generateReport();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private String getFileExtension(String filePath) {
        int dot = filePath.lastIndexOf('.');
        return (dot != -1) ? filePath.substring(dot + 1) : "";
    }

    private void generateReport() {
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
            System.err.println("Error writing indexing report: " + e.getMessage());
        }
    }
}
