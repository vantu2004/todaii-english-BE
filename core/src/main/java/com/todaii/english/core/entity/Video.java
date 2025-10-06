package com.todaii.english.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.todaii.english.shared.enums.CefrLevel;

import java.time.LocalDateTime;

@Entity
@Table(name = "videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "provider_name", length = 191, nullable = false)
	private String providerName;

	@Column(name = "provider_url", length = 1024, nullable = false)
	private String providerUrl;

	@Column(length = 512, nullable = false)
	private String title;

	@Column(name = "author_name", length = 191, nullable = false)
	private String authorName;

	@Column(name = "thumbnail_url", length = 1024, nullable = false)
	private String thumbnailUrl;

	@Lob
	@Column(name = "embed_html", columnDefinition = "LONGTEXT", nullable = false)
	private String embedHtml; // iframe HTML

	private Integer views;

	@Enumerated(EnumType.STRING)
	@Column(name = "cefr_level", length = 32, nullable = false)
	private CefrLevel cefrLevel;

	@Builder.Default
	private Boolean enabled = false;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
}
