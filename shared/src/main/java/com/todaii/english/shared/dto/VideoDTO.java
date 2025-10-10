package com.todaii.english.shared.dto;

import java.util.Set;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.todaii.english.shared.enums.CefrLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {
	@NotBlank
	@Length(max = 512)
	private String title;

	@NotBlank
	@Length(max = 191)
	private String authorName;

	@NotBlank
	@Length(max = 191)
	private String providerName;

	@NotBlank
	@URL
	@Length(max = 1024)
	private String providerUrl;

	@NotBlank
	@URL
	@Length(max = 1024)
	private String thumbnailUrl;

	@NotBlank
	private String embedHtml;

	@NotBlank
	@URL
	@Length(max = 1024)
	private String videoUrl;

	@NotNull
	private CefrLevel cefrLevel;

	@NotEmpty
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Set<Long> topicIds;
}
