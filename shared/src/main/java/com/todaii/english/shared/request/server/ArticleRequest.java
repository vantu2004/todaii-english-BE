package com.todaii.english.shared.request.server;

import com.todaii.english.shared.enums.CefrLevel;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@Builder
public class ArticleRequest {

	@Length(max = 191, message = "Source ID must not exceed 191 characters")
	private String sourceId;

	@NotBlank(message = "Source name cannot be blank")
	@Length(max = 191, message = "Source name must not exceed 191 characters")
	private String sourceName;

	@NotBlank(message = "Author cannot be blank")
	@Length(max = 191, message = "Author must not exceed 191 characters")
	private String author;

	@NotBlank(message = "Title cannot be blank")
	@Length(max = 512, message = "Title must not exceed 512 characters")
	private String title;

	@NotBlank(message = "Description cannot be blank")
	private String description;

	@NotBlank(message = "Source URL cannot be blank")
	@Length(max = 1024, message = "Source URL must not exceed 1024 characters")
	@URL(message = "Source URL must be a valid URL")
	private String sourceUrl;

	@Length(max = 1024, message = "Image URL must not exceed 1024 characters")
	@URL(message = "Image URL must be a valid URL")
	private String imageUrl;

	@NotNull(message = "Published date is required")
	private LocalDateTime publishedAt;

	@NotNull(message = "CEFR level is required")
	private CefrLevel cefrLevel;

	@NotEmpty(message = "At least one topic ID is required")
	private Set<Long> topicIds;
}
