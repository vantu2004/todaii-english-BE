package com.todaii.english.core.port;

import com.todaii.english.shared.response.DictionaryApiResponse;
import com.todaii.english.shared.response.TodaiiEnglishResponse;

public interface DictionaryPort {
  DictionaryApiResponse[] lookupFreeDictionaryApi(String word);
  TodaiiEnglishResponse lookupTodaiiDictionaryApi(String word, int page, int size);
}
