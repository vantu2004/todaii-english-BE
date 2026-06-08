package com.todaii.english.client.notebook;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.DictionaryWord;
import com.todaii.english.core.entity.NotebookItem;
import com.todaii.english.core.repository.DictionaryRepository;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteDictService {
  private final NotebookRepository notebookRepository;
  private final DictionaryRepository dictionaryRepository;

  private NotebookItem getOwned(Long userId, Long id) {
    NotebookItem item =
        notebookRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(404, "Notebook not found"));

    if (!item.getUser().getId().equals(userId)) {
      throw new BusinessException(403, "Forbidden");
    }
    return item;
  }

  public List<DictionaryWord> getEntries(Long userId, Long noteId) {
    NotebookItem notebookItem = getOwned(userId, noteId);
    return new ArrayList<>(notebookItem.getWords());
  }

  public void addEntry(Long userId, Long noteId, String word) {
    NotebookItem notebookItem = getOwned(userId, noteId);

    DictionaryWord dictionaryWord =
        dictionaryRepository
            .findByWord(word)
            .orElseThrow(() -> new BusinessException(404, "Dictionary word not found"));

    notebookItem.getWords().add(dictionaryWord);

    notebookRepository.save(notebookItem);
  }

  public void removeEntry(Long userId, Long noteId, Long entryId) {
    NotebookItem notebookItem = getOwned(userId, noteId);

    notebookItem.getWords().removeIf(e -> e.getId().equals(entryId));

    notebookRepository.save(notebookItem);
  }
}
