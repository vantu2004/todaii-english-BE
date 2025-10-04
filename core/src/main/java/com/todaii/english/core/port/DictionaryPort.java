package com.todaii.english.core.port;

import com.todaii.english.shared.response.DictionaryApiResponse;

public interface DictionaryPort {
	DictionaryApiResponse[] lookupWord(String word);
}
