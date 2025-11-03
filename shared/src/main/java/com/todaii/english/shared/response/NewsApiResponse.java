package com.todaii.english.shared.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsApiResponse {
	private String status;
	private Integer totalResults;
	private List<ArticleData> articles;

	@Getter
	@Setter
	public static class ArticleData {
		private Source source;
		private String author;
		private String title;
		private String description;
		private String url;
		private String urlToImage;
		private String publishedAt;
		private String content;
	}

	@Getter
	@Setter
	public static class Source {
		private String id;
		private String name;
	}
}
