package com.todaii.english.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.todaii.english.shared.enums.CefrLevel;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Article {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// id của nguồn cung cấp bài
	@Column(name = "source_id", length = 191)
	private String sourceId;

	// tên nguồn cung cấp bài
	@Column(name = "source_name", length = 191)
	private String sourceName;

	@Column(length = 191)
	private String author;

	@Column(nullable = false, length = 512)
	private String title;

	@Lob
	@Column(columnDefinition = "MEDIUMTEXT")
	private String description;

	@Column(name = "source_url", length = 1024)
	private String sourceUrl;

	@Column(name = "image_url", length = 1024)
	private String imageUrl;

	// Thời điểm bài báo được xuất bản (từ NewsAPI hoặc admin nhập)
	@Column(name = "published_at")
	private LocalDateTime publishedAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "cefr_level", length = 32)
	private CefrLevel cefrLevel;

	@Builder.Default
	private Integer views = 0;

	@Builder.Default
	private Boolean enabled = false;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private Set<ArticleParagraph> paragraphs = new HashSet<>();

	// quan hệ 1 chiều
	@ManyToMany
	@JoinTable(name = "article_dictionary_entries", joinColumns = @JoinColumn(name = "article_id"), inverseJoinColumns = @JoinColumn(name = "dict_entry_id"))
	@Builder.Default
	private Set<DictionaryEntry> entries = new HashSet<>();

	// quan hệ 1 chiều
	@ManyToMany
	@JoinTable(name = "article_topics", joinColumns = @JoinColumn(name = "article_id"), inverseJoinColumns = @JoinColumn(name = "topic_id"))
	@Builder.Default
	private Set<Topic> topics = new HashSet<>();
}
