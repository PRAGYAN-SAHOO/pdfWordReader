package com.frame.pdf.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;



public interface PdfReaderService {
	String readPDFAndGetContent(MultipartFile file);
	
	String validSpellCheck(MultipartFile file);
	Map<String, Integer> sortByCountAndWords(MultipartFile file);
	public Map<String, Integer>  getNearbyWords(MultipartFile file);
	
}
