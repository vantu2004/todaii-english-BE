package com.todaii.english.core.service;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.UsageStatistic;
import com.todaii.english.core.port.DictionaryPort;
import com.todaii.english.core.port.UsageStatisticPort;
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
}
