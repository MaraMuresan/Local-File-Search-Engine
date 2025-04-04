package assignment2.controller;

import assignment2.model.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.*;

@Profile("master")
@RestController
@RequestMapping("/api")
public class MasterController {

    private final List<String> workerUrls = Arrays.asList(
            "http://localhost:3001/api/search",
            "http://localhost:3002/api/search",
            "http://localhost:3003/api/search",
            "http://localhost:3004/api/search",
            "http://localhost:3005/api/search"
    );

    private final Map<String, List<String>> cache = new ConcurrentHashMap<>();

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/search")
    public List<String> search(@RequestBody SearchRequest request) {
        String query = request.getQuery();

        if (cache.containsKey(query)) {
            return cache.get(query);
        }

        ExecutorService executor = Executors.newFixedThreadPool(workerUrls.size());
        List<Future<List<String>>> futures = new ArrayList<>();

        for (String url : workerUrls) {
            SearchRequest req = new SearchRequest(query);
            futures.add(executor.submit(() ->
                    restTemplate.postForObject(url, req, List.class)
            ));
        }

        List<String> combined = new ArrayList<>();
        for (Future<List<String>> future : futures) {
            try {
                combined.addAll(future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        Collections.sort(combined);
        cache.put(query, combined);
        return combined;
    }
}
