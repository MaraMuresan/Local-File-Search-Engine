package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultMapper {
    public List<String> mapResults(ResultSet rs) throws SQLException {
        List<String> results = new ArrayList<>();
        while (rs.next()) {
            results.add(rs.getString("file_name"));
        }
        return results;
    }
}
