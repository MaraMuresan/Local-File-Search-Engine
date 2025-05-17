package searchcontroller;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class SpellingCorrectorV1 implements CorrectionStrategy {
    private final Map<String, Integer> wordFreq = new HashMap<>();

    public SpellingCorrectorV1(File corpus) throws IOException {
        loadCorpus(corpus);
    }

    private void loadCorpus(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Pattern wordPattern = Pattern.compile("\\w+");
            while ((line = reader.readLine()) != null) {
                Matcher matcher = wordPattern.matcher(line.toLowerCase());
                while (matcher.find()) {
                    String word = matcher.group();
                    wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
                }
            }
        }
    }

    @Override
    public String correct(String word) {
        if (wordFreq.containsKey(word)) return word;
        List<String> candidates = edits1(word);
        String best = word;
        int maxFreq = 0;

        for (String cand : candidates) {
            int freq = wordFreq.getOrDefault(cand, 0);
            if (freq > maxFreq) {
                maxFreq = freq;
                best = cand;
            }
        }
        return best;
    }

    private List<String> edits1(String word) {
        List<String> edits = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            edits.add(word.substring(0, i) + word.substring(i + 1)); //delete
        }
        for (int i = 0; i < word.length() - 1; i++) {
            edits.add(word.substring(0, i) + word.charAt(i + 1) + word.charAt(i) + word.substring(i + 2)); //transpose
        }
        for (int i = 0; i < word.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                edits.add(word.substring(0, i) + c + word.substring(i + 1)); //replace
            }
        }
        for (int i = 0; i <= word.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                edits.add(word.substring(0, i) + c + word.substring(i)); //insert
            }
        }
        return edits;
    }
}
