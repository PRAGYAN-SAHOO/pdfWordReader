package com.frame.pdf.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class WordDistanceAPI {
	private List<String> wordList;

    public WordDistanceAPI(List<String> wordList) {
        this.wordList = wordList;
}
    public Map<String, Integer> getWordsNear(String inputWord, int distanceThreshold) {
        Map<String, Integer> result = new HashMap<>();
        
        for (String word : wordList) {
            int distance = StringUtils.getLevenshteinDistance(inputWord, word);
            if (distance <= distanceThreshold) {
                result.put(word, distance);
            }
        }

        return result;
    }
}