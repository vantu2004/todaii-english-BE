package com.todaii.english.core.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vocab_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VocabGroup {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 191, unique = true, nullable = false)
	private String name;

	@Column(length = 191, unique = true, nullable = false)
	private String alias;

	@Builder.Default
	private Boolean enabled = false;

	@Builder.Default
	@Column(name = "is_deleted")
	private Boolean isDeleted = false;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@ManyToMany(mappedBy = "groups")
	@Builder.Default
	private Set<VocabDeck> decks = new HashSet<>();

}
