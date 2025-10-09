package com.todaii.english.shared.dto;

import java.util.Set;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictionaryEntryDTO {
	@NotNull
	@Length(min = 1, max = 191)
	private String headword;

	@Length(max = 191)
	private String ipa;

	@URL
	@Length(max = 1024)
	private String audioUrl;

	@Valid
	private Set<DictionarySenseDTO> senses;
}
