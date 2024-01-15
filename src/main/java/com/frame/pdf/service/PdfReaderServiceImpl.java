package com.frame.pdf.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.frame.pdf.model.WordDistanceAPI;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.swabunga.spell.event.SpellChecker;

@Service
public class PdfReaderServiceImpl implements PdfReaderService {

	private final Logger logger = LoggerFactory.getLogger(PdfReaderServiceImpl.class);
	private String word;

	

	@Override
	public String readPDFAndGetContent(MultipartFile file) {

		StringBuffer contentOfPDf = new StringBuffer();
		try {
			PdfReader pdfReader = new PdfReader(file.getInputStream());
			logger.info("No of pages - " + pdfReader.getNumberOfPages());

			for (int pageNo = 1; pageNo <= pdfReader.getNumberOfPages(); pageNo++) {
				contentOfPDf.append(PdfTextExtractor.getTextFromPage(pdfReader, pageNo));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new String(contentOfPDf);
	}

	// check spelling errors
	public String validSpellCheck(MultipartFile file) {
		// Check if the word is valid
		String pdfText = readPDFAndGetContent(file);
		String result = "";
		List<String> wordsInPDF = extractWords(pdfText);

		for (String word : wordsInPDF) {
			if (!isValidWord(word)) {
				// result=word;
				System.out.println("Spelling error: " + word);
			}
			result = word;
		}
		return "Spelling error : " + result;
	}

	private List<String> extractWords(String text) {
		// Split text into words using regex
		return Arrays.asList(text.split("\\W+"));
	}

	private boolean isValidWord(String word) {
		List<String> validWords = new ArrayList<>();
		validWords.add(word);
		// Use Levenshtein distance for spell-checking
		int threshold = 2;

		for (String validWord : validWords) {
			int distance = LevenshteinDistance.getDefaultInstance().apply(word, validWord);

			if (distance <= threshold) {
				return true;
			}
		}

		return false;

	}

// API to fetch the word and counts, sorted by count and then word

	public Map<String, Integer> sortByCountAndWords(MultipartFile file) {
		String text = readPDFAndGetContent(file);
		Map<String, Integer> wordCounts = countWords(text);

		// Sort by count and then word using Java streams
		Map<String, Integer> sortedMap = wordCounts.entrySet().stream()
				.sorted(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue).reversed()
						.thenComparing(Map.Entry::getKey))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		// Print and return the sorted map
		sortedMap.forEach((word, count) -> System.out.println("Word: " + word + ", Count: " + count));

		return sortedMap;
	}

	private static Map<String, Integer> countWords(String text) {
		return Arrays.stream(text.split("\\s+"))
				.collect(Collectors.toMap(word -> word, word -> 1, Integer::sum, HashMap::new));
	}

	// API where a word is given, and gives list of words near to the word and their
	// count. (String distance)

	public Map<String, Integer> getNearbyWords(MultipartFile file) {
		int distanceThreshold = 2;

		String content = readPDFAndGetContent(file);
		List<String> words = Collections.singletonList(content);

		Map<String, Integer> combinedResult = new HashMap<>();

		WordDistanceAPI api = new WordDistanceAPI(words);

		for (String word : words) {

			Map<String, Integer> result = api.getWordsNear(word, distanceThreshold);
			combinedResult.putAll(result);

			result.forEach((key, value) -> System.out.println(key + " (Distance: " + value + ")"));
		}

		System.out.println("Combined results: " + combinedResult);

		return new LinkedHashMap<>(combinedResult);
	}
}
