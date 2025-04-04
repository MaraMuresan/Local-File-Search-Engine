package assignment2.controller;

import assignment2.model.SearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Profile({"worker1", "worker2", "worker3", "worker4", "worker5"})
@RestController
@RequestMapping("/api")
public class WorkerController {

    @Value("${worker.root.directory}")
    private String rootDirectory;

    @PostMapping("/search")
    public List<String> search(@RequestBody SearchRequest request) {
        List<String> results = new ArrayList<>();
        searchFiles(new File(rootDirectory), request.getQuery(), results);
        return results;
    }

    private void searchFiles(File dir, String query, List<String> results) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    searchFiles(file, query, results);
                }
            }
        } else if (dir.getName().contains(query)) {
            results.add(dir.getAbsolutePath());
        }
    }
}
