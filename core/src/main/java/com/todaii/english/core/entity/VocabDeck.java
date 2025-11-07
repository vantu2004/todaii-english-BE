package com.todaii.english.core.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.todaii.english.shared.enums.CefrLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vocab_decks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VocabDeck {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 191, nullable = false)
	private String name;

	@Lob
	@Column(columnDefinition = "MEDIUMTEXT", nullable = false)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(length = 32)
	private CefrLevel cefrLevel;

	@Builder.Default
	private Boolean enabled = false;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	// quan hệ 1 chiều
	@ManyToMany
	@JoinTable(name = "deck_groups", joinColumns = @JoinColumn(name = "deck_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
	@Builder.Default
	private Set<VocabGroup> groups = new HashSet<>();

	// quan hệ 1 chiều
	@ManyToMany
	@JoinTable(name = "deck_words", joinColumns = @JoinColumn(name = "deck_id"), inverseJoinColumns = @JoinColumn(name = "dict_entry_id"))
	@Builder.Default
	private Set<DictionaryEntry> words = new HashSet<>();
}
