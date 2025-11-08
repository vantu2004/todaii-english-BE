package com.todaii.english.server.vocabulary;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.core.entity.VocabDeck;
import com.todaii.english.core.entity.VocabGroup;
import com.todaii.english.server.dictionary.DictionaryEntryRepository;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.DeckRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VocabDeckService {
	private final VocabDeckRepository vocabDeckRepository;
	private final DictionaryEntryRepository dictionaryEntryRepository;
	private final VocabGroupRepository vocabGroupRepository;

	public VocabDeck createDraftDeck(DeckRequest deckRequest) {
		VocabDeck vocabDeck = VocabDeck.builder().name(deckRequest.getName()).description(deckRequest.getDescription())
				.cefrLevel(deckRequest.getCefrLevel()).build();

		Set<VocabGroup> groups = new HashSet<>();
		for (Long groupId : deckRequest.getGroupIds()) {
			VocabGroup group = vocabGroupRepository.findById(groupId)
					.orElseThrow(() -> new BusinessException(404, "Group not found: " + groupId));
			groups.add(group);
		}
		vocabDeck.setGroups(groups);

		return vocabDeckRepository.save(vocabDeck);
	}

	@Deprecated
	public List<VocabDeck> findAll() {
		return vocabDeckRepository.findAll();
	}

	public Page<VocabDeck> findAllPaged(int page, int size, String sortBy, String direction, String keyword) {
		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(page - 1, size, sort);

		return vocabDeckRepository.search(null, keyword, pageable);
	}

	public Page<VocabDeck> findByGroupId(Long groupId, int page, int size, String sortBy, String direction,
			String keyword) {
		if (!vocabGroupRepository.existsById(groupId)) {
			throw new BusinessException(404, "Vocabulary group not found");
		}

		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(page - 1, size, sort);

		return vocabDeckRepository.search(groupId, keyword, pageable);
	}

	public VocabDeck findById(Long deckId) {
		return vocabDeckRepository.findById(deckId).orElseThrow(() -> new BusinessException(404, "Deck not found"));
	}

	// ko check trùng vì có nhiều từ giống nhau
	public VocabDeck addWordToDeck(Long deckId, Long wordId) {
		VocabDeck vocabDeck = findById(deckId);

		DictionaryEntry dictionaryEntry = dictionaryEntryRepository.findById(wordId)
				.orElseThrow(() -> new BusinessException(404, "Word not found"));

		vocabDeck.getWords().add(dictionaryEntry);
		return vocabDeckRepository.save(vocabDeck);
	}

	public VocabDeck removeWordFromDeck(Long deckId, Long wordId) {
		VocabDeck vocabDeck = findById(deckId);
		DictionaryEntry dictionaryEntry = dictionaryEntryRepository.findById(wordId)
				.orElseThrow(() -> new BusinessException(404, "Word not found"));

		boolean removed = vocabDeck.getWords().remove(dictionaryEntry);
		if (!removed) {
			throw new BusinessException(400, "Word not found in deck");
		}

		return vocabDeckRepository.save(vocabDeck);
	}

	public VocabDeck updateVocabDeck(Long deckId, DeckRequest deckRequest) {
		VocabDeck vocabDeck = findById(deckId);
		vocabDeck.setName(deckRequest.getName());
		vocabDeck.setDescription(deckRequest.getDescription());
		vocabDeck.setCefrLevel(deckRequest.getCefrLevel());

		Set<VocabGroup> groups = new HashSet<>();
		for (Long groupId : deckRequest.getGroupIds()) {
			VocabGroup group = vocabGroupRepository.findById(groupId)
					.orElseThrow(() -> new BusinessException(404, "Group not found: " + groupId));
			groups.add(group);
		}
		vocabDeck.setGroups(groups);

		return vocabDeckRepository.save(vocabDeck);
	}

	public void toggleEnabled(Long deckId) {
		VocabDeck vocabDeck = findById(deckId);
		if (vocabDeck.getWords().isEmpty()) {
			throw new BusinessException(400, "Cannot enable empty deck");
		}

		vocabDeck.setEnabled(!vocabDeck.getEnabled());

		vocabDeckRepository.save(vocabDeck);
	}

	public void deleteById(Long deckId) {
		if (!vocabDeckRepository.existsById(deckId)) {
			throw new BusinessException(404, "Deck not found");
		}

		vocabDeckRepository.deleteById(deckId);
	}

}
