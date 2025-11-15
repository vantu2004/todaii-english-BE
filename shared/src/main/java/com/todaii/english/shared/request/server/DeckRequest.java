package com.todaii.english.shared.request.server;

import java.util.Set;

import org.hibernate.validator.constraints.Length;

import com.todaii.english.shared.enums.CefrLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DeckRequest {

	@NotBlank(message = "Deck name cannot be blank")
	@Length(max = 191, message = "Deck name must not exceed 191 characters")
	private String name;

	@NotBlank(message = "Description cannot be blank")
	private String description;

	@NotNull(message = "CEFR level is required")
	private CefrLevel cefrLevel;

	@NotEmpty(message = "At least one group ID is required")
	private Set<Long> groupIds;
}
