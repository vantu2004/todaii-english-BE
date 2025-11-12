package com.todaii.english.shared.dto;

import java.util.Set;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictionaryEntryDTO {
	@NotBlank
	@Length(max = 191)
	private String headword;

	@NotBlank
	@Length(max = 191)
	private String ipa;

	@URL
	@Length(max = 1024)
	private String audioUrl;

	@Valid
	@NotEmpty
	private Set<DictionarySenseDTO> senses;
}
