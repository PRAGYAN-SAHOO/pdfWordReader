package com.frame.pdf.controller;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.frame.pdf.service.PdfReaderService;

@RestController
public class PdfReadController {

	// private final Logger logger=LoggerFactory.getLogger(PdfReadController.class);

	@Autowired
	private PdfReaderService pdfReaderService;

	//pdf content api
	@PostMapping("/reader-pdf-get-content")
	public ResponseEntity<String> readPdfForContent(@RequestBody MultipartFile file) {
		return ResponseEntity.ok(pdfReaderService.readPDFAndGetContent(file));
	}

	/*
	 * // not used one
	 * 
	 * @PostMapping("/get-valid-spell-word-count") public ResponseEntity<Map<String,
	 * Integer>> getValidSpellWordCount(@RequestBody MultipartFile file) { return
	 * ResponseEntity.ok(pdfReaderService.findValidSpellWordAndTheirCount(file)); }
	 */

	//	API to fetch the word and counts, sorted by count and then word
	@PostMapping("/fetch-word-count-sort")
	public ResponseEntity<Map<String, Integer>> sortByCountAndWords(@RequestBody MultipartFile file) {
		return ResponseEntity.ok(pdfReaderService.sortByCountAndWords(file));
	}

	// Word reading should be fuzzy and should handle spelling errors as well.	
	
	@PostMapping("get-valid-spell-word-count")
	public ResponseEntity<String> validSpellCheckWords(@RequestBody MultipartFile file) {
		return ResponseEntity.ok(pdfReaderService.validSpellCheck(file));
	}

	// API where a word is given, and gives list of words near to the word and their
	// count. (String distance)

	@PostMapping("/near-by-word")
	public ResponseEntity<Map<String, Integer>> getNearbyWords(MultipartFile file) {
		return   ResponseEntity.ok(pdfReaderService.getNearbyWords(file));
		
	}

}
