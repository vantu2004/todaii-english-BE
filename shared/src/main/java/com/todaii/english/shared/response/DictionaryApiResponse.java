package com.todaii.english.shared.response;

import lombok.Data;
import java.util.List;

@Data
public class DictionaryApiResponse {
	private String word;
	private String phonetic;
	private List<Phonetic> phonetics;
	private List<Meaning> meanings;

	@Data
	public static class Phonetic {
		private String text;
		private String audio;
	}

	@Data
	public static class Meaning {
		private String partOfSpeech;
		private List<Definition> definitions;
	}

	@Data
	public static class Definition {
		private String definition;
		private String example;
		private List<String> synonyms;
		private List<String> antonyms;
	}
}
