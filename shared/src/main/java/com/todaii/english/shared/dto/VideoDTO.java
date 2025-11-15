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

	@NotBlank(message = "Title cannot be blank")
	@Length(max = 512, message = "Title must not exceed 512 characters")
	private String title;

	@NotBlank(message = "Author name cannot be blank")
	@Length(max = 191, message = "Author name must not exceed 191 characters")
	private String authorName;

	@NotBlank(message = "Provider name cannot be blank")
	@Length(max = 191, message = "Provider name must not exceed 191 characters")
	private String providerName;

	@NotBlank(message = "Provider URL cannot be blank")
	@URL(message = "Provider URL must be a valid URL")
	@Length(max = 1024, message = "Provider URL must not exceed 1024 characters")
	private String providerUrl;

	@NotBlank(message = "Thumbnail URL cannot be blank")
	@URL(message = "Thumbnail URL must be a valid URL")
	@Length(max = 1024, message = "Thumbnail URL must not exceed 1024 characters")
	private String thumbnailUrl;

	@NotBlank(message = "Embed HTML cannot be blank")
	private String embedHtml;

	@NotBlank(message = "Video URL cannot be blank")
	@URL(message = "Video URL must be a valid URL")
	@Length(max = 1024, message = "Video URL must not exceed 1024 characters")
	private String videoUrl;

	@NotNull(message = "CEFR level is required")
	private CefrLevel cefrLevel;

	@NotEmpty(message = "At least one topic ID is required")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Set<Long> topicIds;
}
