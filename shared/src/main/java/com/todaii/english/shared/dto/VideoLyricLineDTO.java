package com.todaii.english.shared.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VideoLyricLineDTO {
	@NotNull(message = "lineOrder is required")
	@Min(value = 1, message = "lineOrder must be >= 1")
	private Integer lineOrder;

	@NotNull(message = "startMs is required")
	@Min(value = 0, message = "startMs must be >= 0")
	private Integer startMs;

	@NotNull(message = "endMs is required")
	@Min(value = 0, message = "endMs must be >= 0")
	private Integer endMs;

	@NotBlank
	private String textEn;

	@NotBlank
	private String textVi;
}
