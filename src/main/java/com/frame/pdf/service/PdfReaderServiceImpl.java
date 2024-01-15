package com.frame.pdf.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

	// not used
	@Override
	public Map<String, Integer> findValidSpellWordAndTheirCount(MultipartFile file) {
		String fileContent = readPDFAndGetContent(file);
		// logger.info(fileContent);
		SpellChecker spellChecker = new SpellChecker();
		// spellChecker.addDictionary(new Gen);
		// spellChecker.isSpellingCorrect("Buiillm");

		// System.out.println("spellChecker *** :"+spellChecker.getSuggestions("ABABA",
		// 2));

		Map<String, Long> correctWords = new HashMap<>();
		for (String eachWord : Arrays.asList(fileContent.split(" "))) {
			System.out.println(eachWord);
			System.out.println(spellChecker.isCorrect(eachWord));
			if (spellChecker.isCorrect(eachWord)) {
				correctWords.put(eachWord, correctWords.getOrDefault(eachWord, 0l) + 1);
			}
		}

		Map<String, Integer> nonReaptWordLength = new HashMap<>();
		for (String key : correctWords.keySet()) {
			nonReaptWordLength.put(key, key.length());
		}
		return nonReaptWordLength;
	}

	// check spelling errors 
	public String validSpellCheck(MultipartFile file) {
		// Check if the word is valid
		String pdfText = readPDFAndGetContent(file);
		String result = "";
		List<String> wordsInPDF = extractWords(pdfText);

		for (String word : wordsInPDF) {
			if (!isValidWord(word)) {
				//result=word;
				System.out.println("Spelling error: " + word);
			}
			result=word;
		}
		return "Spelling error : "+result ;
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
		// Fetch words and count occurrences
		String text = readPDFAndGetContent(file);
		Map<String, Integer> wordCounts = countWords(text);

		// Sort by count and then word
		List<Map.Entry<String, Integer>> sortedList = sortWordCounts(wordCounts);
		Map<String, Integer> sortedMap = new LinkedHashMap<>();
		// Print sorted word counts
		for (Map.Entry<String, Integer> entry : sortedList) {
			System.out.println("Word: " + entry.getKey() + ", Count: " + entry.getValue());
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		// return "Word: " + entry.getKey() + ", Count: " + entry.getValue();

		// Return the sorted map

		return sortedMap;
	}

// count word
	private static Map<String, Integer> countWords(String text) {
		Map<String, Integer> wordCounts = new HashMap<>();

		String[] words = text.split("\\s+");
		for (String word : words) {
			wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
		}

		return wordCounts;
	}

	// sort word count
	private static List<Map.Entry<String, Integer>> sortWordCounts(Map<String, Integer> wordCounts) {
		List<Map.Entry<String, Integer>> entries = new ArrayList<>(wordCounts.entrySet());

		// Custom comparator to sort by count (descending) and then word (ascending)
		Comparator<Map.Entry<String, Integer>> comparator = Comparator.<Map.Entry<String, Integer>>comparingInt(
				Map.Entry::getValue).reversed().thenComparing(Map.Entry::getKey);

		entries.sort(comparator);

		return entries;

	}

	// API where a word is given, and gives list of words near to the word and their
	// count. (String distance)

	public Map<String, Integer>  getNearbyWords(MultipartFile file) {
		List<String> words = new ArrayList<>();
		Map<String, Integer> result =new HashMap<>();
		int distanceThreshold = 2;
		String word = readPDFAndGetContent(file);
		words.add(word);
		WordDistanceAPI api = new WordDistanceAPI(words);
		String inputWord = "javac";
		for(String input:words) {
		

		result= api.getWordsNear(input, distanceThreshold);
		System.out.println("Result :"+result);
		}
		Map<String, Integer> sortedMap = new LinkedHashMap<>();

		System.out.println("Words near '" + inputWord + "' with distance <= " + distanceThreshold + ":");
		for (Map.Entry<String, Integer> entry : result.entrySet()) {
			sortedMap.put(entry.getKey(),entry.getValue());
			System.out.println(entry.getKey() + " (Distance: " + entry.getValue() + ")");
		}
		return sortedMap;
	}
}
