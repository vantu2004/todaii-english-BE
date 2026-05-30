package com.todaii.english.core.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.DictionaryWord;
import com.todaii.english.core.entity.UsageStatistic;
import com.todaii.english.core.port.DictionaryPort;
import com.todaii.english.core.port.UsageStatisticPort;
import com.todaii.english.core.port.redis.RedisPort;
import com.todaii.english.core.port.redis.RedisRankingPort;
import com.todaii.english.core.repository.DictionaryRepository;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.RedisType;
import com.todaii.english.shared.enums.UsageType;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.DictionaryApiResponse;
import com.todaii.english.shared.response.TodaiiEnglishResponse;
import com.todaii.english.shared.response.TopWordResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryService {
  private final DictionaryPort dictionaryPort;
  private final DictionaryRepository dictionaryRepository;
  private final UsageStatisticPort usageStatisticPort;
  private final RedisPort redisPort;
  private final RedisRankingPort redisRankingPort;
  private final ObjectMapper objectMapper;

  public TodaiiEnglishResponse searchByTodaiiDictionary(
      Long currentAdminId, String word, int page, int size) {
    return searchWord(currentAdminId, word, page, size);
  }

  public DictionaryApiResponse[] searchByFreeDictionaryApi(Long currentAdminId, String word) {
    DictionaryApiResponse[] dictionaryApiResponses = dictionaryPort.lookupFreeDictionaryApi(word);
    if (dictionaryApiResponses != null && dictionaryApiResponses.length > 0) {
      redisRankingPort.incrementWordSearchCount(word);
    }

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

  public List<TopWordResponse> getTopWords() {
    return redisRankingPort.getTopWords(20);
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

  public List<String> getAiSuggestions(String word, Long currentAdminId) {
    return dictionaryPort.getAiSuggestions(word, currentAdminId, ActorType.ADMIN);
  }

  // Luồng tìm kiếm chính (Orchestration Flow)
  private TodaiiEnglishResponse searchWord(
      Long currentAdminId, String rawWord, int page, int size) {
    String word = rawWord.trim().toLowerCase();

    // TH1: có trong redis
    String cachedJson = redisPort.get(RedisType.DICT_WORD, word);
    if (cachedJson != null) {
      log.info("Cache Hit for word: {}", word);

      redisRankingPort.incrementWordSearchCount(word);
      redisPort.refreshTtlIfNeeded(RedisType.DICT_WORD, word);

      return parseJsonToObject(cachedJson);
    }

    // TH2: có trong db
    DictionaryWord dictionaryWord =
        dictionaryRepository.findByWord(word).orElseGet(DictionaryWord::new);
    String jsonData =
        StringUtils.hasText(dictionaryWord.getJsonData()) ? dictionaryWord.getJsonData() : null;
    if (StringUtils.hasText(jsonData)) {
      log.info("DB Hit for word: {}", word);

      redisRankingPort.incrementWordSearchCount(word);
      redisPort.set(RedisType.DICT_WORD, word, jsonData);

      return parseJsonToObject(jsonData);
    }

    // TH3: jsonData null nên call todaii api để lấy
    log.info("Cache Miss & DB Miss. Calling External API for word: {}", word);
    TodaiiEnglishResponse todaiiEnglishResponse =
        getTodaiiEnglishResponse(currentAdminId, word, page, size);

    jsonData = convertObjectToJson(todaiiEnglishResponse);

    dictionaryWord.setWord(word);
    dictionaryWord.setJsonData(jsonData);

    dictionaryRepository.save(dictionaryWord);

    redisPort.set(RedisType.DICT_WORD, word, jsonData);
    redisRankingPort.incrementWordSearchCount(word);

    return todaiiEnglishResponse;
  }

  private TodaiiEnglishResponse getTodaiiEnglishResponse(
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

  private String convertObjectToJson(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new BusinessException(500, "Error serializing API response");
    }
  }

  private TodaiiEnglishResponse parseJsonToObject(String json) {
    try {
      return objectMapper.readValue(json, TodaiiEnglishResponse.class);
    } catch (JsonProcessingException e) {
      throw new BusinessException(500, "Error deserializing stored JSON");
    }
  }
}
