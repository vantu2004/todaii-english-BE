package com.todaii.english.shared.dto;

import java.util.Set;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import com.todaii.english.shared.enums.CefrLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
	private Set<Long> topicIds;
}
