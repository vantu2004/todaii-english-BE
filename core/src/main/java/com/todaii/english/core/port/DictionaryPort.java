package com.todaii.english.core.port;

import java.util.List;

import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.response.DictionaryApiResponse;
import com.todaii.english.shared.response.TodaiiEnglishResponse;

public interface DictionaryPort {
  DictionaryApiResponse[] lookupFreeDictionaryApi(String word);

  TodaiiEnglishResponse lookupTodaiiDictionaryApi(String word, int page, int size);

  List<String> getAiSuggestions(String word, Long actorId, ActorType actorType);
}
