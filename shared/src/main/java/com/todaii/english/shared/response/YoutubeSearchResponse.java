package com.todaii.english.shared.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class YoutubeSearchResponse {
	private String kind;
	private String etag;
	private String nextPageToken;
	private String regionCode;
	private PageInfo pageInfo;
	private List<Item> items;

	@Getter
	@Setter
	public static class PageInfo {
		private int totalResults;
		private int resultsPerPage;
	}

	@Getter
	@Setter
	public static class Item {
		private String kind;
		private String etag;
		private Id id;
		private Snippet snippet;
	}

	@Getter
	@Setter
	public static class Id {
		private String kind; // "youtube#video" hoặc "youtube#playlist"
		private String videoId;
		private String playlistId;
	}

	@Getter
	@Setter
	public static class Snippet {
		private String publishedAt;
		private String channelId;
		private String title;
		private String description;
		private Map<String, Thumbnail> thumbnails; // key: default, medium, high
		private String channelTitle;
		private String liveBroadcastContent;
		private String publishTime;
	}

	@Getter
	@Setter
	public static class Thumbnail {
		private String url;
		private int width;
		private int height;
	}
}
