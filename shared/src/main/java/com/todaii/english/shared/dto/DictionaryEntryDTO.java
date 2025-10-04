package com.todaii.english.shared.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DictionaryEntryDTO {
	private String headword;
	private String ipa;
	@JsonProperty("audio_url")
	private String audioUrl;
	private List<DictionarySenseDTO> senses;
}
