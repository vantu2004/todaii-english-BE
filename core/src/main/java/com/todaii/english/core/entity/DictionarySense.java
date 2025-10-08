package com.todaii.english.core.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.todaii.english.shared.enums.PartOfSpeech;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "dictionary_senses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DictionarySense {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private PartOfSpeech pos;

	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String meaning;

	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String definition;

	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String example;

	private List<String> synonyms;

	private List<String> collocations;

	@ManyToOne
	@JoinColumn(name = "entry_id", nullable = false)
	@JsonIgnore
	private DictionaryEntry entry;

}
