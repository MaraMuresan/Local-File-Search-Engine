package filesystem;

import config.DatabaseConfig;
import org.apache.tika.Tika;
import ranking.RankingBoostStrategy;
import report.ReportGenerationStrategy;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.sql.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;

public class FileIndexer {

    private static final Tika tika = new Tika();

    private static final String DB_URL = DatabaseConfig.getUrl();
    private static final String DB_USER = DatabaseConfig.getUser();
    private static final String DB_PASS = DatabaseConfig.getPassword();

    private final List<String> indexedFiles = new ArrayList<>();
    private final List<String> skippedFiles = new ArrayList<>();
    private final List<String> failedFiles = new ArrayList<>();

    private final ReportGenerationStrategy reportStrategy;
    private final RankingBoostStrategy boostStrategy;

    public FileIndexer(ReportGenerationStrategy reportStrategy, RankingBoostStrategy boostStrategy) {
        this.reportStrategy = reportStrategy;
        this.boostStrategy = boostStrategy;
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
                            FileTime fileTime = Files.getLastModifiedTime(file);
                            Timestamp timestamp = new Timestamp(fileTime.toMillis());
                            long size = Files.size(file); //bytes
                            float rankScore = computeRankScore(file, size, timestamp);

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

    private float computeRankScore(Path file, long size, Timestamp timestamp) {
        String path = file.toString().toLowerCase();
        float baseScore = 0;

        baseScore += 100.0 / (path.length() + 1);

        if (path.contains("lab")) baseScore += 10;
        if (path.contains("software")) baseScore += 5;
        if (path.contains("image")) baseScore += 3;

        if (path.endsWith(".java")) baseScore += 2;
        if (path.endsWith(".txt")) baseScore += 1;

        return boostStrategy.applyBoost(baseScore, size, timestamp);
    }


    private void generateReport() {
        reportStrategy.generateReport(indexedFiles, skippedFiles, failedFiles);
    }

}
