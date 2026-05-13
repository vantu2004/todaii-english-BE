package com.todaii.english.client.dictionary;

import org.springframework.stereotype.Service;

import com.todaii.english.core.port.DictionaryPort;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.TodaiiEnglishResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodaiiDictionaryService {
  private final DictionaryPort dictionaryPort;

  public TodaiiEnglishResponse search(String word, int page, int size) {
    TodaiiEnglishResponse todaiiEnglishResponse =
        dictionaryPort.lookupTodaiiDictionaryApi(word, page, size);

    if (todaiiEnglishResponse.getTotal().equals(0L)
        && Boolean.FALSE.equals(todaiiEnglishResponse.getFound())) {
      throw new BusinessException(404, "Word not found");
    }

    return todaiiEnglishResponse;
  }
}
