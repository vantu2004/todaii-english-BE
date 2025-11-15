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

	@NotBlank(message = "Headword cannot be blank")
	@Length(max = 191, message = "Headword must not exceed 191 characters")
	private String headword;

	@NotBlank(message = "IPA cannot be blank")
	@Length(max = 191, message = "IPA must not exceed 191 characters")
	private String ipa;

	@URL(message = "Audio URL must be a valid URL format")
	@Length(max = 1024, message = "Audio URL must not exceed 1024 characters")
	private String audioUrl;

	@Valid
	@NotEmpty(message = "At least one sense is required")
	private Set<DictionarySenseDTO> senses;
}
