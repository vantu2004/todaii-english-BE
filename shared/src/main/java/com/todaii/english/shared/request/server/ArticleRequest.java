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
	@Length(max = 191)
	private String sourceId;

	@NotBlank
	@Length(max = 191)
	private String sourceName;

	@NotBlank
	@Length(max = 191)
	private String author;

	@NotBlank
	@Length(max = 512)
	private String title;

	@NotBlank
	private String description;

	@NotBlank
	@Length(max = 1024)
	@URL
	private String sourceUrl;

	@Length(max = 1024)
	@URL
	private String imageUrl;

	@NotNull
	private LocalDateTime publishedAt;

	@NotNull
	private CefrLevel cefrLevel;

	@NotEmpty
	private Set<Long> topicIds;

	// dùng khi update vì khi tạo thì chưa có content để lọc từ
	private Set<Long> dictionaryEntryIds;
}
