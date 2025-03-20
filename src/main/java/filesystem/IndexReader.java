package filesystem;

import java.util.ArrayList;
import java.util.List;

public class IndexReader {
    private List<String> index = new ArrayList<>();

    public boolean contains(String query) {
        return index.contains(query);
    }

    public String search(String query) {
        return "Found in local index: " + query;
    }
}
