package com.todaii.english.server.vocabulary;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	public List<VocabDeck> findAll() {
		return vocabDeckRepository.findAll();
	}

	public VocabDeck findById(Long deckId) {
		return vocabDeckRepository.findById(deckId).orElseThrow(() -> new BusinessException(404, "Deck not found"));
	}

	public VocabDeck addWordToDeck(Long deckId, Long wordId) {
		VocabDeck vocabDeck = findById(deckId);
		DictionaryEntry dictionaryEntry = dictionaryEntryRepository.findById(wordId)
				.orElseThrow(() -> new BusinessException(404, "Word not found"));

		// kiểm tra trùng
		boolean exists = vocabDeck.getWords().stream().anyMatch(w -> w.getId().equals(wordId));
		if (!exists) {
			vocabDeck.getWords().add(dictionaryEntry);
			vocabDeckRepository.save(vocabDeck);
		}

		return vocabDeck;
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
