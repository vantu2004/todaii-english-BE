package com.todaii.english.shared.dto;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictionarySenseDTO {

	@NotBlank(message = "POS cannot be blank")
	@Length(max = 32, message = "POS must not exceed 32 characters")
	private String pos;

	@NotBlank(message = "Meaning (Vietnamese) cannot be blank")
	private String meaning;

	@NotBlank(message = "Definition (English) cannot be blank")
	private String definition;

	@NotBlank(message = "Example sentence cannot be blank")
	private String example;

	@Size(max = 10, message = "Maximum 10 synonyms allowed")
	private List<String> synonyms;

	@Size(max = 10, message = "Maximum 10 collocations allowed")
	private List<String> collocations;
}
