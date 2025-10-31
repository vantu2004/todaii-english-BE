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
	@NotBlank
	@Length(max = 191)
	private String name;

	@NotBlank
	private String description;

	@NotNull
	private CefrLevel cefrLevel;

	@NotEmpty
	private Set<Long> groupIds;
}
