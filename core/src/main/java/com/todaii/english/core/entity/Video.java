package com.todaii.english.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.todaii.english.shared.enums.CefrLevel;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Video {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 512, nullable = false)
	private String title;

	@Column(name = "author_name", length = 191, nullable = false)
	private String authorName;

	@Column(name = "provider_name", length = 191, nullable = false)
	private String providerName;

	@Column(name = "provider_url", length = 1024, nullable = false)
	private String providerUrl;

	@Column(name = "thumbnail_url", length = 1024, nullable = false)
	private String thumbnailUrl;

	@Lob
	@Column(name = "embed_html", columnDefinition = "LONGTEXT", nullable = false)
	private String embedHtml; // iframe HTML

	@Column(name = "video_url", length = 1024, nullable = false)
	private String videoUrl;

	@Builder.Default
	private Integer views = 0;

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

	// Quan hệ N-N với topic (1 chiều)
	@ManyToMany
	@JoinTable(name = "videos_topics", joinColumns = @JoinColumn(name = "video_id"), inverseJoinColumns = @JoinColumn(name = "topic_id"))
	@Builder.Default
	private Set<Topic> topics = new HashSet<>();
}
