package com.todaii.english.server.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.Article;
import com.todaii.english.shared.enums.CefrLevel;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.ArticleRequest;
import com.todaii.english.shared.response.NewsApiResponse;
import com.todaii.english.server.security.TestSecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.*;

@WebMvcTest(controllers = ArticleApiController.class)
@Import(TestSecurityConfig.class)
class ArticleApiControllerTests {

	private static final String BASE_ENDPOINT = "/api/v1/article";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ArticleService articleService;

	@Autowired
	private ObjectMapper objectMapper;

	// Helper: tạo ArticleRequest hợp lệ
	private ArticleRequest createValidRequest() {
		return ArticleRequest.builder().sourceId("abc123").sourceName("BBC").author("John Doe").title("Breaking News")
				.description("Something happened today.").sourceUrl("https://bbc.com/article")
				.imageUrl("https://bbc.com/image.jpg").publishedAt(LocalDateTime.now()).cefrLevel(CefrLevel.B1)
				.topicIds(Set.of(1L)).build();
	}

	// -------- GET /news-api ----------
	@Test
	void fetchArticles_shouldReturnOk() throws Exception {
		NewsApiResponse mockResponse = new NewsApiResponse();

		given(articleService.fetchFromNewsApi(anyString(), anyInt(), anyInt(), anyString())).willReturn(mockResponse);

		mockMvc.perform(get(BASE_ENDPOINT + "/news-api").param("query", "tech").param("pageSize", "5")
				.param("page", "1").param("sortBy", "relevancy")).andExpect(status().isOk());
	}

	@Test
	void fetchArticles_shouldThrowLimitExceeded() throws Exception {
		mockMvc.perform(
				get(BASE_ENDPOINT + "/news-api").param("query", "ai").param("pageSize", "60").param("page", "3"))
				.andExpect(status().isBadRequest());
	}

	// -------- GET / ------------
	@Test
	void getAllArticles_shouldReturnList() throws Exception {
		Article article = Article.builder().id(1L).title("News").build();
		
		given(articleService.findAll()).willReturn(List.of(article));

		mockMvc.perform(get(BASE_ENDPOINT)).andExpect(status().isOk()).andExpect(jsonPath("$[0].title").value("News"));
	}

	@Test
	void getAllArticles_shouldReturnNoContent() throws Exception {
		given(articleService.findAll()).willReturn(Collections.emptyList());

		mockMvc.perform(get(BASE_ENDPOINT)).andExpect(status().isNoContent());
	}

	// -------- GET /{id} ----------
	@Test
	void getArticleById_shouldReturnOk() throws Exception {
		Article article = Article.builder().id(1L).title("News").build();
		
		given(articleService.findById(1L)).willReturn(article);

		mockMvc.perform(get(BASE_ENDPOINT + "/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("News"));
	}

	@Test
	void getArticleById_shouldThrowNotFound() throws Exception {
		given(articleService.findById(99L)).willThrow(new BusinessException(404, "Article not found"));

		mockMvc.perform(get(BASE_ENDPOINT + "/99")).andExpect(status().isNotFound());
	}

	// -------- POST / ----------
	@Test
	void createArticle_shouldReturnCreated() throws Exception {
		ArticleRequest req = createValidRequest();
		Article article = Article.builder().id(1L).title(req.getTitle()).build();

		given(articleService.create(any(ArticleRequest.class))).willReturn(article);

		mockMvc.perform(post(BASE_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.title").value(req.getTitle()));
	}

	@Test
	void createArticle_shouldFailValidation() throws Exception {
		ArticleRequest invalid = createValidRequest();
		invalid.setTitle(""); // NotBlank violated

		mockMvc.perform(post(BASE_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalid))).andExpect(status().isBadRequest());
	}

	// -------- PUT /{id} ----------
	@Test
	void updateArticle_shouldReturnOk() throws Exception {
		ArticleRequest req = createValidRequest();
		Article updated = Article.builder().id(1L).title("Updated").build();
		
		given(articleService.update(eq(1L), any(ArticleRequest.class))).willReturn(updated);

		mockMvc.perform(put(BASE_ENDPOINT + "/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Updated"));
	}

	// -------- PATCH /{id}/enabled ----------
	@Test
	void toggleEnabled_shouldReturnOk() throws Exception {
		willDoNothing().given(articleService).toggleEnabled(1L);

		mockMvc.perform(patch(BASE_ENDPOINT + "/1/enabled")).andExpect(status().isOk());
	}

	@Test
	void toggleEnabled_shouldThrowBusinessException() throws Exception {
		willThrow(new BusinessException(400, "Cannot enable the article because it has no content"))
				.given(articleService).toggleEnabled(1L);

		mockMvc.perform(patch(BASE_ENDPOINT + "/1/enabled")).andExpect(status().isBadRequest());
	}

	// -------- DELETE /{id} ----------
	@Test
	void deleteArticle_shouldReturnOk() throws Exception {
		willDoNothing().given(articleService).deleteById(1L);

		mockMvc.perform(delete(BASE_ENDPOINT + "/1")).andExpect(status().isOk());
	}
}
