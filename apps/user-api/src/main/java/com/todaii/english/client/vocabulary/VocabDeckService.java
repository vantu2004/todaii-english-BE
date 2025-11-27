package com.todaii.english.client.vocabulary;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.VocabDeck;
import com.todaii.english.shared.enums.CefrLevel;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VocabDeckService {
	private final VocabDeckRepository vocabDeckRepository;

	public List<VocabDeck> findAll() {
		return vocabDeckRepository.findAll();
	}

	public VocabDeck findById(Long id) {
		return vocabDeckRepository.findById(id)
				.orElseThrow(() -> new BusinessException(404, "Vocabulary deck not found"));
	}

	public Page<VocabDeck> filterVocabDecks(String keyword, CefrLevel cefrLevel, String alias, int page, int size,
			String sortBy, String direction) {
		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);

		Pageable pageable = PageRequest.of(page - 1, size, sort);

		Specification<VocabDeck> spec = VocabDeckSpecification.isEnabled()
				.and(VocabDeckSpecification.hasKeyword(keyword)).and(VocabDeckSpecification.hasCefrLevel(cefrLevel))
				.and(VocabDeckSpecification.hasGroup(alias));

		return vocabDeckRepository.findAll(spec, pageable);
	}

}
