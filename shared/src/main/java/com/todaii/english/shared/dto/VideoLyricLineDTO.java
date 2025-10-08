package com.todaii.english.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoLyricLineDTO {
	@NotNull(message = "lineOrder is required")
	@Min(value = 1, message = "lineOrder must be >= 1")
	@JsonProperty("line_order")
	private Integer lineOrder;

	@NotNull(message = "startMs is required")
	@Min(value = 0, message = "startMs must be >= 0")
	@JsonProperty("start_ms")
	private Integer startMs;

	@NotNull(message = "endMs is required")
	@Min(value = 0, message = "endMs must be >= 0")
	@JsonProperty("end_ms")
	private Integer endMs;

	@JsonProperty("text_en")
	private String textEn;

	@JsonProperty("text_vi")
	private String textVi;
}
