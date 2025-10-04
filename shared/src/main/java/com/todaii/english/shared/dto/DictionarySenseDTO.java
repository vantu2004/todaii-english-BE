package com.todaii.english.shared.dto;

import java.util.List;

import lombok.Data;

@Data
public class DictionarySenseDTO {
	private String pos;
	private String meaning; // tiếng Việt
	private String definition; // tiếng Anh
	private String example;
	private List<String> synonyms;
	private List<String> collocations;
}
