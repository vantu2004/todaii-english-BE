package com.todaii.english.server.video;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.VideoLyricLine;
import com.todaii.english.server.exception.GlobalExceptionHandler;
import com.todaii.english.server.security.TestSecurityConfig;
import com.todaii.english.shared.dto.VideoLyricLineDTO;
import com.todaii.english.shared.exceptions.BusinessException;

@WebMvcTest(VideoLyricLineApiController.class)
@Import({ TestSecurityConfig.class, GlobalExceptionHandler.class })
class VideoLyricLineApiControllerTests {

	private static final String BASE = "/api/v1/video";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private VideoLyricLineService service;

	private final VideoLyricLine lyric = VideoLyricLine.builder().id(1L).lineOrder(1).startMs(1000).endMs(3000)
			.textEn("Hello").textVi("Xin chào").build();

	private final VideoLyricLineDTO dto = VideoLyricLineDTO.builder().lineOrder(1).startMs(1000).endMs(3000)
			.textEn("Hello").textVi("Xin chào").build();

	// ==========================================================
	// 1️ POST /lyric/import
	// ==========================================================
	@Test
	@DisplayName("POST /lyric/import — Upload file hợp lệ → 200 OK")
	void testImportLyrics_Success() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "test.srt", "text/plain", "mock".getBytes());
		when(service.importFromSrt(any())).thenReturn(List.of(dto));

		mockMvc.perform(multipart(BASE + "/lyric/import").file(file)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].textEn").value("Hello"));
	}

	@Test
	@DisplayName("POST /lyric/import — File rỗng → 500 InternalServerError")
	void testImportLyrics_EmptyFile() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "bad.srt", "text/plain", new byte[0]);
		when(service.importFromSrt(any())).thenThrow(new RuntimeException("Uploaded file is empty"));

		mockMvc.perform(multipart(BASE + "/lyric/import").file(file)).andExpect(status().isInternalServerError());
	}

	// ==========================================================
	// 2️ GET /{videoId}/lyric
	// ==========================================================
	@Test
	@DisplayName("GET /{videoId}/lyric — Có lyrics → 200 OK")
	void testGetAllLyrics_Success() throws Exception {
		when(service.findAll(1L, "id", "asc", "")).thenReturn(List.of(lyric));

		mockMvc.perform(get(BASE + "/1/lyric")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].textEn").value("Hello"));
	}

	@Test
	@DisplayName("GET /{videoId}/lyric — Không có lyrics → 204 NoContent")
	void testGetAllLyrics_NoContent() throws Exception {
		when(service.findAll(1L, "id", "asc", "")).thenReturn(List.of());

		mockMvc.perform(get(BASE + "/1/lyric")).andExpect(status().isNoContent());
	}

	// ==========================================================
	// 3️ GET /lyric/{lyricId}
	// ==========================================================
	@Test
	@DisplayName("GET /lyric/{lyricId} — Tìm thấy lyric → 200 OK")
	void testGetLyric_Success() throws Exception {
		when(service.findById(5L)).thenReturn(lyric);

		mockMvc.perform(get(BASE + "/lyric/5")).andExpect(status().isOk())
				.andExpect(jsonPath("$.textEn").value("Hello"));
	}

	@Test
	@DisplayName("GET /lyric/{lyricId} — Không tìm thấy → 404 NotFound")
	void testGetLyric_NotFound() throws Exception {
		when(service.findById(5L)).thenThrow(new BusinessException(404, "Lyric not found"));

		mockMvc.perform(get(BASE + "/lyric/5")).andExpect(status().isNotFound());
	}

	// ==========================================================
	// 4️ POST /{videoId}/lyric/batch
	// ==========================================================
	@Test
	@DisplayName("POST /{videoId}/lyric/batch — Thành công → 201 Created")
	void testCreateBatch_Success() throws Exception {
		when(service.createBatch(eq(1L), any())).thenReturn(List.of(lyric));

		mockMvc.perform(post(BASE + "/1/lyric/batch").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(List.of(dto)))).andExpect(status().isCreated())
				.andExpect(jsonPath("$[0].textVi").value("Xin chào"));
	}

	@Test
	@DisplayName("POST /{videoId}/lyric/batch — Dữ liệu không hợp lệ → 400 BadRequest")
	void testCreateBatch_ValidationError() throws Exception {
		VideoLyricLineDTO invalid = VideoLyricLineDTO.builder().lineOrder(0).startMs(-1).endMs(-1).textEn("").textVi("")
				.build();

		mockMvc.perform(post(BASE + "/1/lyric/batch").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(List.of(invalid)))).andExpect(status().isBadRequest());
	}

	// ==========================================================
	// 5️ PUT /lyric/{lyricId}
	// ==========================================================
	@Test
	@DisplayName("PUT /lyric/{lyricId} — Cập nhật thành công → 200 OK")
	void testUpdateLyric_Success() throws Exception {
		when(service.updateLyric(eq(5L), any())).thenReturn(lyric);

		mockMvc.perform(put(BASE + "/lyric/5").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.textEn").value("Hello"));
	}

	@Test
	@DisplayName("PUT /lyric/{lyricId} — Không tồn tại → 404 NotFound")
	void testUpdateLyric_NotFound() throws Exception {
		when(service.updateLyric(eq(5L), any())).thenThrow(new BusinessException(404, "Lyric not found"));

		mockMvc.perform(put(BASE + "/lyric/5").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isNotFound());
	}

	// ==========================================================
	// 6️ DELETE /lyric/{lyricId}
	// ==========================================================
	@Test
	@DisplayName("DELETE /lyric/{lyricId} — Xóa thành công → 204 NoContent")
	void testDeleteLyric_Success() throws Exception {
		doNothing().when(service).deleteLyric(5L);

		mockMvc.perform(delete(BASE + "/lyric/5")).andExpect(status().isNoContent());

		verify(service).deleteLyric(5L);
	}

	@Test
	@DisplayName("DELETE /lyric/{lyricId} — Không tìm thấy → 404 NotFound")
	void testDeleteLyric_NotFound() throws Exception {
		doThrow(new BusinessException(404, "Lyric not found")).when(service).deleteLyric(5L);

		mockMvc.perform(delete(BASE + "/lyric/5")).andExpect(status().isNotFound());
	}

	// ==========================================================
	// 7️ DELETE /{videoId}/lyric
	// ==========================================================
	@Test
	@DisplayName("DELETE /{videoId}/lyric — Xóa toàn bộ lyric → 204 NoContent")
	void testDeleteAllLyrics_Success() throws Exception {
		doNothing().when(service).deleteAllLyrics(1L);

		mockMvc.perform(delete(BASE + "/1/lyric")).andExpect(status().isNoContent());

		verify(service).deleteAllLyrics(1L);
	}

	@Test
	@DisplayName("DELETE /{videoId}/lyric — Service lỗi → 500 InternalServerError")
	void testDeleteAllLyrics_Error() throws Exception {
		doThrow(new RuntimeException("Unexpected error")).when(service).deleteAllLyrics(1L);

		mockMvc.perform(delete(BASE + "/1/lyric")).andExpect(status().isInternalServerError());
	}
}
