package filesystem;

import org.apache.tika.Tika;
import report.ReportGenerationStrategy;

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

    private final ReportGenerationStrategy reportStrategy;

    public FileIndexer(ReportGenerationStrategy reportStrategy) {
        this.reportStrategy = reportStrategy;
    }

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
                            float rankScore = computeRankScore(file);

                            PreparedStatement stmt = conn.prepareStatement("""
                                INSERT INTO file_index (file_path, content, extension, tags, timestamp, size, index_file_name, index_content, rank_score)
                                VALUES (?, ?, ?, ?, ?, ?, to_tsvector('english', ?), to_tsvector('english', ?), ?)
                                ON CONFLICT (file_path) DO UPDATE 
                                SET content = EXCLUDED.content,
                                extension = EXCLUDED.extension,
                                tags = EXCLUDED.tags,
                                timestamp = EXCLUDED.timestamp,
                                size = EXCLUDED.size,
                                index_file_name = EXCLUDED.index_file_name,
                                index_content = EXCLUDED.index_content,
                                rank_score = EXCLUDED.rank_score
                        """);


                            stmt.setString(1, filePath);
                            stmt.setString(2, content);
                            stmt.setString(3, extension);
                            stmt.setString(4, tags);
                            stmt.setTimestamp(5, timestamp);
                            stmt.setLong(6, size);
                            stmt.setString(7, filePath);
                            stmt.setString(8, content);
                            stmt.setFloat(9, rankScore);

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

    private float computeRankScore(Path file) {
        String path = file.toString().toLowerCase();
        float score = 0;

        score += 100.0 / (path.length() + 1);

        if (path.contains("lab")) score += 10;
        if (path.contains("software")) score += 5;
        if (path.contains("image")) score += 3;

        if (path.endsWith(".java")) score += 2;
        if (path.endsWith(".txt")) score += 1;

        return score;
    }

    private void generateReport() {
        reportStrategy.generateReport(indexedFiles, skippedFiles, failedFiles);
    }

}
