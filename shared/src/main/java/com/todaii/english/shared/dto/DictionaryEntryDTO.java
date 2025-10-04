package com.todaii.english.shared.dto;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DictionaryEntryDTO {
	@NotNull
	@Length(min = 1, max = 191)
	private String headword;

	@Length(max = 191)
	private String ipa;

	@URL
	@Length(max = 1024)
	@JsonProperty("audio_url")
	private String audioUrl;

	@Valid
	private List<DictionarySenseDTO> senses;
}
