package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryExecutor {
    private static final String URL = "jdbc:postgresql://localhost:5432/LocalFileSearchEngine";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public Map<String, List<String[]>> searchWithQualifiers(Map<String, String> qualifiers) {
        Map<String, List<String[]>> resultMap = new HashMap<>();
        List<String[]> pathMatches = new ArrayList<>();
        List<String[]> contentMatches = new ArrayList<>();

        String pathQuery = qualifiers.getOrDefault("path", "").trim();
        String contentQuery = qualifiers.getOrDefault("content", "").trim();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            if (!pathQuery.isEmpty()) {
                String[] keywords = pathQuery.split("\\s*(?i)AND\\s*");

                String pathSql = "SELECT file_path, content FROM file_index WHERE " +
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
                        pathMatches.add(new String[]{filePath, content});
                    }
                }
            }


            if (!contentQuery.isEmpty()) {
                String contentSql = """
                SELECT file_path, content
                FROM file_index
                WHERE index_content @@ plainto_tsquery(?)
            """;

                try (PreparedStatement stmt = conn.prepareStatement(contentSql)) {
                    stmt.setString(1, contentQuery);

                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        String filePath = rs.getString("file_path");
                        String content = rs.getString("content");

                        if (pathMatches.stream().noneMatch(f -> f[0].equals(filePath))) {
                            contentMatches.add(new String[]{filePath, content});
                        }
                    }
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
