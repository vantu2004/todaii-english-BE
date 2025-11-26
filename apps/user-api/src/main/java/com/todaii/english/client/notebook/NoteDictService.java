package com.todaii.english.client.notebook;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.todaii.english.client.dictionary.DictionaryRepository;
import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.core.entity.NotebookItem;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteDictService {
	private final NotebookRepository notebookRepository;
	private final DictionaryRepository dictionaryRepository;

	private NotebookItem findById(Long id) {
		return notebookRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Notebook not found"));
	}

	public List<DictionaryEntry> getEntries(Long noteId) {
		NotebookItem notebookItem = findById(noteId);

		return new ArrayList<>(notebookItem.getWords());
	}

	public NotebookItem addEntry(Long noteId, Long entryId) {
		NotebookItem notebookItem = findById(noteId);
		DictionaryEntry dictionaryEntry = dictionaryRepository.findById(entryId)
				.orElseThrow(() -> new BusinessException(404, "Dictionary entry not found"));

		notebookItem.getWords().add(dictionaryEntry);

		return notebookRepository.save(notebookItem);
	}

	public NotebookItem removeEntry(Long noteId, Long entryId) {
		NotebookItem notebookItem = findById(noteId);
		notebookItem.getWords().removeIf(e -> e.getId().equals(entryId));

		return notebookRepository.save(notebookItem);
	}

}
