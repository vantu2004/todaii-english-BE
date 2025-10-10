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

	private static final String ENDPOINT = "/api/v1/lyric";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private VideoLyricLineService videoLyricLineService;

	// Mẫu dữ liệu dùng chung
	private final VideoLyricLine lyric = VideoLyricLine.builder().id(1L).lineOrder(1).startMs(1000).endMs(3000)
			.textEn("Hello").textVi("Xin chào").build();

	private final VideoLyricLineDTO dto = VideoLyricLineDTO.builder().lineOrder(1).startMs(1000).endMs(3000)
			.textEn("Hello").textVi("Xin chào").build();

	// ============================================================
	// 1️ POST /import
	// ============================================================

	@Test
	@DisplayName("POST /import — Upload file hợp lệ → 200 OK")
	void testImportLyrics_Success() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "test.srt", "text/plain",
				"1\n00:00:01,000 --> 00:00:03,000\nHello\nXin chào\n".getBytes());

		when(videoLyricLineService.importFromSrt(any())).thenReturn(List.of(dto));

		mockMvc.perform(multipart(ENDPOINT + "/import").file(file)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].textEn").value("Hello"));

		verify(videoLyricLineService).importFromSrt(any());
	}

	@Test
	@DisplayName("POST /import — File lỗi → 500 InternalServerError")
	void testImportLyrics_Error() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "bad.srt", "text/plain", new byte[0]);

		when(videoLyricLineService.importFromSrt(any())).thenThrow(new RuntimeException("Uploaded file is empty"));

		mockMvc.perform(multipart(ENDPOINT + "/import").file(file)).andExpect(status().isInternalServerError());
	}

	// ============================================================
	// 2️ GET /video/{videoId}
	// ============================================================

	@Test
	@DisplayName("GET /video/{videoId} — Có lyrics → 200 OK")
	void testGetAllLyrics_Success() throws Exception {
		when(videoLyricLineService.findAll(1L)).thenReturn(List.of(lyric));

		mockMvc.perform(get(ENDPOINT + "/video/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].textEn").value("Hello"));
	}

	@Test
	@DisplayName("GET /video/{videoId} — Không có lyrics → 204 NoContent")
	void testGetAllLyrics_NoContent() throws Exception {
		when(videoLyricLineService.findAll(1L)).thenReturn(List.of());

		mockMvc.perform(get(ENDPOINT + "/video/1")).andExpect(status().isNoContent());
	}

	// ============================================================
	// 3️ GET /video/{videoId}/line/{lineId}
	// ============================================================

	@Test
	@DisplayName("GET /video/{videoId}/line/{lineId} — Tìm thấy lyric → 200 OK")
	void testGetLyric_Success() throws Exception {
		when(videoLyricLineService.findByVideoIdAndLineId(1L, 5L)).thenReturn(lyric);

		mockMvc.perform(get(ENDPOINT + "/video/1/line/5")).andExpect(status().isOk())
				.andExpect(jsonPath("$.textEn").value("Hello"));
	}

	@Test
	@DisplayName("GET /video/{videoId}/line/{lineId} — Không tìm thấy → 404 NotFound")
	void testGetLyric_NotFound() throws Exception {
		when(videoLyricLineService.findByVideoIdAndLineId(1L, 5L))
				.thenThrow(new BusinessException(404, "Lyric not found in this video"));

		mockMvc.perform(get(ENDPOINT + "/video/1/line/5")).andExpect(status().isNotFound());
	}

	// ============================================================
	// 4️ POST /video/{videoId}/batch
	// ============================================================

	@Test
	@DisplayName("POST /video/{videoId}/batch — Tạo batch lyrics hợp lệ → 201 Created")
	void testCreateBatch_Success() throws Exception {
		when(videoLyricLineService.createBatch(eq(1L), any())).thenReturn(List.of(lyric));

		mockMvc.perform(post(ENDPOINT + "/video/1/batch").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(List.of(dto)))).andExpect(status().isCreated())
				.andExpect(jsonPath("$[0].textVi").value("Xin chào"));
	}

	@Test
	@DisplayName("POST /video/{videoId}/batch — Dữ liệu không hợp lệ → 400 BadRequest")
	void testCreateBatch_ValidationError() throws Exception {
		VideoLyricLineDTO invalid = VideoLyricLineDTO.builder().lineOrder(0) // invalid
				.startMs(-1).endMs(-1).textEn("").textVi("").build();

		mockMvc.perform(post(ENDPOINT + "/video/1/batch").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(List.of(invalid)))).andExpect(status().isBadRequest());
	}

	// ============================================================
	// 5️ PUT /video/{videoId}/line/{lineId}
	// ============================================================

	@Test
	@DisplayName("PUT /video/{videoId}/line/{lineId} — Cập nhật thành công → 200 OK")
	void testUpdateLyric_Success() throws Exception {
		when(videoLyricLineService.updateLyric(eq(1L), eq(5L), any())).thenReturn(lyric);

		mockMvc.perform(put(ENDPOINT + "/video/1/line/5").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.textEn").value("Hello"));
	}

	@Test
	@DisplayName("PUT /video/{videoId}/line/{lineId} — Lyric không tồn tại → 404 NotFound")
	void testUpdateLyric_NotFound() throws Exception {
		when(videoLyricLineService.updateLyric(eq(1L), eq(5L), any()))
				.thenThrow(new BusinessException(404, "Lyric not found"));

		mockMvc.perform(put(ENDPOINT + "/video/1/line/5").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isNotFound());
	}

	// ============================================================
	// 6️ DELETE /video/{videoId}/line/{lineId}
	// ============================================================

	@Test
	@DisplayName("DELETE /video/{videoId}/line/{lineId} — Xóa thành công → 204 NoContent")
	void testDeleteLyric_Success() throws Exception {
		doNothing().when(videoLyricLineService).deleteLyric(1L, 5L);

		mockMvc.perform(delete(ENDPOINT + "/video/1/line/5")).andExpect(status().isNoContent());

		verify(videoLyricLineService).deleteLyric(1L, 5L);
	}

	@Test
	@DisplayName("DELETE /video/{videoId}/line/{lineId} — Không tìm thấy → 404 NotFound")
	void testDeleteLyric_NotFound() throws Exception {
		doThrow(new BusinessException(404, "Lyric not found")).when(videoLyricLineService).deleteLyric(1L, 5L);

		mockMvc.perform(delete(ENDPOINT + "/video/1/line/5")).andExpect(status().isNotFound());
	}

	// ============================================================
	// 7️ DELETE /video/{videoId}
	// ============================================================

	@Test
	@DisplayName("DELETE /video/{videoId} — Xóa toàn bộ lyric → 204 NoContent")
	void testDeleteAllLyrics_Success() throws Exception {
		doNothing().when(videoLyricLineService).deleteAllLyrics(1L);

		mockMvc.perform(delete(ENDPOINT + "/video/1")).andExpect(status().isNoContent());

		verify(videoLyricLineService).deleteAllLyrics(1L);
	}

	@Test
	@DisplayName("DELETE /video/{videoId} — Service lỗi → 500 InternalServerError")
	void testDeleteAllLyrics_Error() throws Exception {
		doThrow(new RuntimeException("Unexpected error")).when(videoLyricLineService).deleteAllLyrics(1L);

		mockMvc.perform(delete(ENDPOINT + "/video/1")).andExpect(status().isInternalServerError());
	}
}
