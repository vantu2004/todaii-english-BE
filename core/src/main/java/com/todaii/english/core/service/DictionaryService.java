package com.todaii.english.core.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.DictionaryWord;
import com.todaii.english.core.entity.UsageStatistic;
import com.todaii.english.core.port.DictionaryPort;
import com.todaii.english.core.port.UsageStatisticPort;
import com.todaii.english.core.repository.DictionaryRepository;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.UsageType;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.DictionaryApiResponse;
import com.todaii.english.shared.response.TodaiiEnglishResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DictionaryService {
  private final DictionaryPort dictionaryPort;
  private final DictionaryRepository dictionaryRepository;
  private final UsageStatisticPort usageStatisticPort;

  public TodaiiEnglishResponse searchByTodaiiDictionary(
      Long currentAdminId, String word, int page, int size) {
    TodaiiEnglishResponse todaiiEnglishResponse =
        dictionaryPort.lookupTodaiiDictionaryApi(word, page, size);

    if (todaiiEnglishResponse.getTotal().equals(0L)
        && Boolean.FALSE.equals(todaiiEnglishResponse.getFound())) {
      throw new BusinessException(404, "Word not found");
    }

    UsageStatistic dictionaryStatistic =
        usageStatisticPort.createDictionaryStatistic(
            currentAdminId, ActorType.ADMIN, UsageType.TODAII_DICT_REQUEST);
    usageStatisticPort.createUsageStatistic(dictionaryStatistic);

    return todaiiEnglishResponse;
  }

  public DictionaryApiResponse[] searchByFreeDictionaryApi(Long currentAdminId, String word) {
    DictionaryApiResponse[] dictionaryApiResponses = dictionaryPort.lookupFreeDictionaryApi(word);

    UsageStatistic dictionaryStatistic =
        usageStatisticPort.createDictionaryStatistic(
            currentAdminId, ActorType.ADMIN, UsageType.DICTIONARY_API_REQUEST);
    usageStatisticPort.createUsageStatistic(dictionaryStatistic);

    return dictionaryApiResponses;
  }

  public DictionaryWord getWordById(Long id) {
    return dictionaryRepository
        .findById(id)
        .orElseThrow(() -> new BusinessException(404, "Dictionary word not found with ID: " + id));
  }

  // Phải bắt buộc có page và size. Chặn size quá lớn để tránh sập RAM.
  public Page<DictionaryWord> searchInDb(String prefix, int page, int size) {
    String keyword = (prefix == null) ? "" : prefix.trim().toLowerCase();

    // Giới hạn tối đa size trả về (VD: max 100) để chống hacker spam RAM
    int safeSize = Math.min(size, 100);

    PageRequest pageRequest = PageRequest.of(page - 1, safeSize, Sort.by("word").ascending());

    return dictionaryRepository.findByWordStartingWith(keyword, pageRequest);
  }

  // Phân trang bằng Cursor
  public List<DictionaryWord> getAllWordsByCursor(Long lastId, int size) {
    int safeSize = Math.min(size, 100);

    // Dùng Pageable chỉ để lấy Limit/Size, không dùng Offset
    PageRequest pageRequest = PageRequest.of(0, safeSize);

    // Nếu là lần đầu tiên gọi (lastId = 0), nó sẽ lấy từ đầu
    Long cursor = (lastId == null) ? 0L : lastId;

    return dictionaryRepository.findNextPageByCursor(cursor, pageRequest);
  }

  public DictionaryWord createWord(String rawWord) {
    String word = rawWord.trim().toLowerCase();

    if (dictionaryRepository.existsByWord(word)) {
      throw new BusinessException(409, "Word already exists in dictionary: " + word);
    }

    DictionaryWord newWord = new DictionaryWord();
    newWord.setWord(word);

    return dictionaryRepository.save(newWord);
  }

  public DictionaryWord updateWord(Long id, String newRawWord) {
    String newWord = newRawWord.trim();

    DictionaryWord existingEntity = getWordById(id);
    if (existingEntity.getWord().equals(newWord)) {
      return existingEntity;
    }

    if (dictionaryRepository.existsByWord(newWord)) {
      throw new BusinessException(409, "Update failed. Word already exists: " + newWord);
    }

    existingEntity.setWord(newWord);

    return dictionaryRepository.save(existingEntity);
  }

  public void deleteWord(Long id) {
    DictionaryWord existingEntity = getWordById(id);
    dictionaryRepository.delete(existingEntity);
  }
}
