package com.todaii.english.shared.dto;

import java.util.Set;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonProperty;
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
	@JsonProperty("author_name")
	private String authorName;

	@NotBlank
	@Length(max = 191)
	@JsonProperty("provider_name")
	private String providerName;

	@NotBlank
	@URL
	@Length(max = 1024)
	@JsonProperty("provider_url")
	private String providerUrl;

	@NotBlank
	@URL
	@Length(max = 1024)
	@JsonProperty("thumbnail_url")
	private String thumbnailUrl;

	@NotBlank
	@JsonProperty("embed_html")
	private String embedHtml;

	@NotBlank
	@URL
	@Length(max = 1024)
	@JsonProperty("video_url")
	private String videoUrl;

	@NotNull
	@JsonProperty("cefr_level")
	private CefrLevel cefrLevel;

	private Boolean enabled = false;
	private Integer views = 0;

	@NotEmpty
	@JsonProperty("topic_ids")
	private Set<Long> topicIds;
}
