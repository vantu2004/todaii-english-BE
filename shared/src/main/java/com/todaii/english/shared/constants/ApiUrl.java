package com.todaii.english.shared.constants;

public class ApiUrl {
	public static final String YOUTUBEOEMBED_BASE_URL = "https://www.youtube.com/oembed?url=<URL>&format=<FORMAT>";
	public static final String DICTIONARY_BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/en";
	public static final String VIDEO_BASE_URL = "https://www.youtube.com/watch?v=%s";
	public static final String PLAYLIST_BASE_URL = "https://www.youtube.com/playlist?list=%s";
	public static final String YOUTUBE_DATA_API_V3_VIDEO = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=%s&maxResults=%d&type=video&key=%s";
	public static final String YOUTUBE_DATA_API_V3_PLAYLIST = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=%s&maxResults=%d&type=playlist&key=%s";
}
