package com.todaii.english.shared.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictionarySenseDTO {
	@NotBlank
	private String pos;

	@NotBlank
	private String meaning; // tiếng Việt

	@NotBlank
	private String definition; // tiếng Anh

	@NotBlank
	private String example;

	@Size(max = 10)
	private List<String> synonyms;

	@Size(max = 10)
	private List<String> collocations;
}
