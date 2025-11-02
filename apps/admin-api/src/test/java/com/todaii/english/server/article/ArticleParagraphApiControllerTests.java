package com.todaii.english.server.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.ArticleParagraph;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.ArticleParagraphRequest;
import com.todaii.english.server.security.TestSecurityConfig;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ArticleParagraphApiController.class)
@Import(TestSecurityConfig.class)
class ArticleParagraphApiControllerTests {

	private static final String BASE_ENDPOINT = "/api/v1/article";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ArticleParagraphService articleParagraphService;

	@Autowired
	private ObjectMapper objectMapper;

	// Helper tạo request hợp lệ
	private ArticleParagraphRequest createValidRequest() {
		ArticleParagraphRequest req = new ArticleParagraphRequest();
		req.setParaOrder(1);
		req.setTextEn("This is an English paragraph for testing.");
		req.setTextViSystem("Đây là đoạn tiếng Việt thử nghiệm.");
		return req;
	}

	// --------------------------------------------------------------------
	// GET /{articleId}/paragraph
	// --------------------------------------------------------------------
	@Test
	@DisplayName("GET /{articleId}/paragraph — Có dữ liệu → 200 OK")
	void getByArticleId_shouldReturnList() throws Exception {
		ArticleParagraph para = ArticleParagraph.builder().id(1L).paraOrder(1).textEn("Hello").build();
		given(articleParagraphService.getByArticleId(1L)).willReturn(List.of(para));

		mockMvc.perform(get(BASE_ENDPOINT + "/1/paragraph")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].textEn").value("Hello"));
	}

	@Test
	@DisplayName("GET /{articleId}/paragraph — Không có dữ liệu → 204 NoContent")
	void getByArticleId_shouldReturnNoContent() throws Exception {
		given(articleParagraphService.getByArticleId(1L)).willReturn(Collections.emptyList());

		mockMvc.perform(get(BASE_ENDPOINT + "/1/paragraph")).andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("GET /{articleId}/paragraph — Article không tồn tại → 404 NotFound")
	void getByArticleId_shouldThrowNotFound() throws Exception {
		given(articleParagraphService.getByArticleId(1L)).willThrow(new BusinessException(404, "Article not found"));

		mockMvc.perform(get(BASE_ENDPOINT + "/1/paragraph")).andExpect(status().isNotFound());
	}

	// --------------------------------------------------------------------
	// POST /{articleId}/paragraph
	// --------------------------------------------------------------------
	@Test
	@DisplayName("POST /{articleId}/paragraph — Tạo thành công → 201 Created")
	void create_shouldReturnCreated() throws Exception {
		ArticleParagraphRequest req = createValidRequest();
		ArticleParagraph saved = ArticleParagraph.builder().id(10L).paraOrder(1).textEn(req.getTextEn()).build();

		given(articleParagraphService.create(eq(1L), any(ArticleParagraphRequest.class))).willReturn(saved);

		mockMvc.perform(post(BASE_ENDPOINT + "/1/paragraph").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(10));
	}

	@Test
	@DisplayName("POST /{articleId}/paragraph — Validation lỗi → 400 BadRequest")
	void create_shouldFailValidation() throws Exception {
		ArticleParagraphRequest req = new ArticleParagraphRequest();
		req.setParaOrder(null);
		req.setTextEn(""); // invalid

		mockMvc.perform(post(BASE_ENDPOINT + "/1/paragraph").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /{articleId}/paragraph — Article không tồn tại → 404 NotFound")
	void create_shouldThrowNotFound() throws Exception {
		given(articleParagraphService.create(eq(99L), any(ArticleParagraphRequest.class)))
				.willThrow(new BusinessException(404, "Article not found"));

		mockMvc.perform(post(BASE_ENDPOINT + "/99/paragraph").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createValidRequest()))).andExpect(status().isNotFound());
	}

	// --------------------------------------------------------------------
	// POST /paragraph/translate
	// --------------------------------------------------------------------
	@Test
	@DisplayName("POST /paragraph/translate — Dịch thành công → 200 OK")
	void translate_shouldReturnTranslatedText() throws Exception {
		given(articleParagraphService.translateParagraph(anyString())).willReturn("Đây là bản dịch tiếng Việt.");

		mockMvc.perform(post(BASE_ENDPOINT + "/paragraph/translate").param("textEn", "This is an English paragraph."))
				.andExpect(status().isOk()).andExpect(content().string("Đây là bản dịch tiếng Việt."));
	}

	@Test
	@DisplayName("POST /paragraph/translate — Thiếu param → 400 BadRequest")
	void translate_shouldFailValidation() throws Exception {
		mockMvc.perform(post(BASE_ENDPOINT + "/paragraph/translate")).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /paragraph/translate — AI lỗi → 500 InternalServerError")
	void translate_shouldThrowException() throws Exception {
		given(articleParagraphService.translateParagraph(anyString()))
				.willThrow(new BusinessException(500, "Translation failed"));

		mockMvc.perform(post(BASE_ENDPOINT + "/paragraph/translate").param("textEn", "This is an English paragraph."))
				.andExpect(status().isInternalServerError());
	}

	// --------------------------------------------------------------------
	// PUT /paragraph/{paragraphId}
	// --------------------------------------------------------------------
	@Test
	@DisplayName("PUT /paragraph/{id} — Cập nhật thành công → 200 OK")
	void update_shouldReturnOk() throws Exception {
		ArticleParagraph updated = ArticleParagraph.builder().id(1L).textEn("Updated text").build();
		given(articleParagraphService.update(eq(1L), any(ArticleParagraphRequest.class))).willReturn(updated);

		mockMvc.perform(put(BASE_ENDPOINT + "/paragraph/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createValidRequest()))).andExpect(status().isOk())
				.andExpect(jsonPath("$.textEn").value("Updated text"));
	}

	@Test
	@DisplayName("PUT /paragraph/{id} — Không tồn tại → 404 NotFound")
	void update_shouldThrowNotFound() throws Exception {
		given(articleParagraphService.update(eq(1L), any(ArticleParagraphRequest.class)))
				.willThrow(new BusinessException(404, "Paragraph not found"));

		mockMvc.perform(put(BASE_ENDPOINT + "/paragraph/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createValidRequest()))).andExpect(status().isNotFound());
	}

	// --------------------------------------------------------------------
	// DELETE /paragraph/{paragraphId}
	// --------------------------------------------------------------------
	@Test
	@DisplayName("DELETE /paragraph/{id} — Xóa thành công → 204 NoContent")
	void delete_shouldReturnNoContent() throws Exception {
		willDoNothing().given(articleParagraphService).delete(1L);

		mockMvc.perform(delete(BASE_ENDPOINT + "/paragraph/1")).andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("DELETE /paragraph/{id} — Không tồn tại → 404 NotFound")
	void delete_shouldThrowNotFound() throws Exception {
		willThrow(new BusinessException(404, "Paragraph not found")).given(articleParagraphService).delete(1L);

		mockMvc.perform(delete(BASE_ENDPOINT + "/paragraph/1")).andExpect(status().isNotFound());
	}
}
