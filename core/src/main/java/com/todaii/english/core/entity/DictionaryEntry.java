package com.todaii.english.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "dictionary_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DictionaryEntry {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 191)
	private String headword; // Từ gốc

	@Column(length = 191)
	private String ipa; // phiên âm

	@Column(name = "audio_url", length = 1024)
	private String audioUrl; // audio phát âm

	@Column(name = "is_deleted")
	@Builder.Default
	private Boolean isDeleted = false;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	// Quan hệ 1-N: 1 entry có nhiều sense
	@OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DictionarySense> senses;
}
