package com.todaii.english.shared.dto;

import java.time.LocalDateTime;

import com.todaii.english.shared.enums.CefrLevel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleDTO {
	private Long id;
	private String sourceId;
	private String sourceName;
	private String author;
	private String title;
	private String description;
	private String sourceUrl;
	private String imageUrl;
	private LocalDateTime publishedAt;
	private CefrLevel cefrLevel;
	private Integer views;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
