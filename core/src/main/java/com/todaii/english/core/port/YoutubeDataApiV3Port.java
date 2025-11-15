package com.todaii.english.core.port;

import com.todaii.english.shared.response.YoutubeSearchResponse;

public interface YoutubeDataApiV3Port {
	YoutubeSearchResponse fetchFromYoutube(String keyword, String type, int size);
}
