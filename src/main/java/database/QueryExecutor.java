package database;

import config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryExecutor {
    private static final String URL = DatabaseConfig.getUrl();
    private static final String USER = DatabaseConfig.getUser();
    private static final String PASSWORD = DatabaseConfig.getPassword();

    public Map<String, List<String[]>> searchWithQualifiers(Map<String, String> qualifiers, Map<String, Integer> frequencyMap) {
        Map<String, List<String[]>> resultMap = new HashMap<>();
        List<String[]> pathMatches = new ArrayList<>();
        List<String[]> contentMatches = new ArrayList<>();

        String pathQuery = qualifiers.getOrDefault("path", "").trim();
        String contentQuery = qualifiers.getOrDefault("content", "").trim();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            if (!pathQuery.isEmpty()) {
                String[] keywords = pathQuery.split("\\s*(?i)AND\\s*");

                String pathSql = "SELECT file_path, content, rank_score, extension, timestamp FROM file_index WHERE " +
                        String.join(" AND ", java.util.Collections.nCopies(keywords.length, "file_path ILIKE ?"));


                try (PreparedStatement stmt = conn.prepareStatement(pathSql)) {
                    for (int i = 0; i < keywords.length; i++) {
                        String param = "%" + keywords[i].trim() + "%";
                        stmt.setString(i + 1, param);
                    }

                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        String filePath = rs.getString("file_path");
                        String content = rs.getString("content");
                        float rankScore = rs.getFloat("rank_score");
                        String extension = rs.getString("extension");
                        String timestamp = rs.getString("timestamp");

                        for (String keyword : keywords) {
                            String key = "path:" + keyword.trim().toLowerCase();
                            int freq = frequencyMap.getOrDefault(key, 0);
                            rankScore += freq * 5;
                        }

                        pathMatches.add(new String[]{filePath, content, String.valueOf(rankScore), extension, timestamp});
                    }

                    pathMatches.sort((a, b) -> Float.compare(Float.parseFloat(b[2]), Float.parseFloat(a[2])));
                }
            }


            if (!contentQuery.isEmpty()) {
                String contentSql = """
                SELECT file_path, content, rank_score, extension, timestamp 
                FROM file_index
                WHERE index_content @@ plainto_tsquery(?)
            """;

                try (PreparedStatement stmt = conn.prepareStatement(contentSql)) {
                    stmt.setString(1, contentQuery);

                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        String filePath = rs.getString("file_path");
                        String content = rs.getString("content");
                        float rankScore = rs.getFloat("rank_score");
                        String extension = rs.getString("extension");
                        String timestamp = rs.getString("timestamp");

                        String[] keywords = contentQuery.split("\\s*(?i)AND\\s*");
                        for (String keyword : keywords) {
                            String key = "content:" + keyword.trim().toLowerCase();
                            int freq = frequencyMap.getOrDefault(key, 0);
                            rankScore += freq * 3;
                        }

                        if (pathMatches.stream().noneMatch(f -> f[0].equals(filePath))) {
                            contentMatches.add(new String[]{filePath, content, String.valueOf(rankScore), extension, timestamp});
                        }
                    }

                    contentMatches.sort((a, b) -> Float.compare(Float.parseFloat(b[2]), Float.parseFloat(a[2])));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        resultMap.put("path", pathMatches);
        resultMap.put("content", contentMatches);
        return resultMap;
    }
}
