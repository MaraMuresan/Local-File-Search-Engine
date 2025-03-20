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

    public Map<String, List<String[]>> searchInDatabase(String query) {
        Map<String, List<String[]>> resultMap = new HashMap<>();
        List<String[]> nameMatches = new ArrayList<>();
        List<String[]> contentMatches = new ArrayList<>();

        String sql = """
        SELECT file_name, content,
               file_name ILIKE ? AS is_name_match,
               index_content @@ plainto_tsquery(?) AS is_content_match
        FROM file_index
        WHERE file_name ILIKE ? OR index_content @@ plainto_tsquery(?)
    """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, query);
            stmt.setString(3, "%" + query + "%");
            stmt.setString(4, query);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String fileName = rs.getString("file_name");
                String content = rs.getString("content");
                boolean nameMatch = rs.getBoolean("is_name_match");
                boolean contentMatch = rs.getBoolean("is_content_match");

                if (nameMatch) nameMatches.add(new String[]{fileName, content});
                else if (contentMatch) contentMatches.add(new String[]{fileName, content});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        resultMap.put("name", nameMatches);
        resultMap.put("content", contentMatches);
        return resultMap;
    }

}
