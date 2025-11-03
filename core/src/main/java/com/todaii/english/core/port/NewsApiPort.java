package com.todaii.english.core.port;

import com.todaii.english.shared.response.NewsApiResponse;

public interface NewsApiPort {

	/**
	 * Gọi NewsAPI (endpoint: /v2/everything)
	 * 
	 * @param query    Từ khóa tìm kiếm (vd: "bitcoin")
	 * @param pageSize Số lượng bài tối đa mỗi trang (1–100)
	 * @param page     Trang kết quả (mặc định 1)
	 * @param sortBy   "publishedAt" | "popularity" | "relevancy"
	 * @return NewsApiResponse
	 */
	NewsApiResponse fetchFromNewsApi(String query, int pageSize, int page, String sortBy);
}
