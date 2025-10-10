package com.todaii.english.server.video;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.Video;
import com.todaii.english.server.security.TestSecurityConfig;
import com.todaii.english.shared.dto.VideoDTO;
import com.todaii.english.shared.enums.CefrLevel;
import com.todaii.english.shared.exceptions.BusinessException;

@WebMvcTest(VideoApiController.class)
@Import(TestSecurityConfig.class)
public class VideoApiControllerTests {

	private static final String ENDPOINT = "/api/v1/video";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private VideoService videoService;

	private Video mockVideo(Long id) {
		return Video.builder().id(id).title("Mock Video").authorName("Tester").providerName("YouTube")
				.providerUrl("https://www.youtube.com/").thumbnailUrl("https://i.ytimg.com/mock.jpg")
				.embedHtml("<iframe src='mock'></iframe>").videoUrl("https://youtube.com/watch?v=123")
				.cefrLevel(CefrLevel.B1).enabled(true).build();
	}

	private VideoDTO mockVideoDTO() {
		VideoDTO dto = new VideoDTO();
		dto.setTitle("Mock Title");
		dto.setAuthorName("Tester");
		dto.setProviderName("YouTube");
		dto.setProviderUrl("https://www.youtube.com/");
		dto.setThumbnailUrl("https://i.ytimg.com/mock.jpg");
		dto.setEmbedHtml("<iframe src='mock'></iframe>");
		dto.setVideoUrl("https://youtube.com/watch?v=123");
		dto.setCefrLevel(CefrLevel.A1);
		dto.setTopicIds(Set.of(1L, 2L));
		return dto;
	}

	// ----------------------------
	// GET /fetch
	// ----------------------------
	@Test
	@DisplayName("GET /fetch - import video thành công")
	void testImportFromYoutubeSuccess() throws Exception {
		VideoDTO videoDTO = mockVideoDTO();
		given(videoService.importFromYoutube(anyString())).willReturn(videoDTO);

		mockMvc.perform(get(ENDPOINT + "/fetch").param("url", "https://youtube.com/watch?v=abc"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Mock Title"));

		then(videoService).should().importFromYoutube(anyString());
	}

	@Test
	@DisplayName("GET /fetch - lỗi BadRequestException khi URL sai")
	void testImportFromYoutubeBadRequest() throws Exception {
		given(videoService.importFromYoutube(anyString())).willThrow(new BadRequestException("Invalid URL"));

		mockMvc.perform(get(ENDPOINT + "/fetch").param("url", "invalid_url")).andExpect(status().isBadRequest());
	}

	// ----------------------------
	// GET /{id}
	// ----------------------------
	@Test
	@DisplayName("GET /{id} - trả về video tồn tại")
	void testGetVideoFound() throws Exception {
		given(videoService.findById(1L)).willReturn(mockVideo(1L));

		mockMvc.perform(get(ENDPOINT + "/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Mock Video"));
	}

	@Test
	@DisplayName("GET /{id} - video không tồn tại")
	void testGetVideoNotFound() throws Exception {
		given(videoService.findById(1L)).willThrow(new BusinessException(404, "Video not found"));

		mockMvc.perform(get(ENDPOINT + "/1")).andExpect(status().isNotFound());
	}

	// ----------------------------
	// GET /
	// ----------------------------
	@Test
	@DisplayName("GET / - trả về danh sách video")
	void testGetAllVideosHasContent() throws Exception {
		given(videoService.findAll()).willReturn(List.of(mockVideo(1L), mockVideo(2L)));

		mockMvc.perform(get(ENDPOINT)).andExpect(status().isOk()).andExpect(jsonPath("$[0].title").value("Mock Video"));
	}

	@Test
	@DisplayName("GET / - không có video nào (204)")
	void testGetAllVideosEmpty() throws Exception {
		given(videoService.findAll()).willReturn(Collections.emptyList());

		mockMvc.perform(get(ENDPOINT)).andExpect(status().isNoContent());
	}

	// ----------------------------
	// POST /
	// ----------------------------
	@Test
	@DisplayName("POST / - tạo video thành công")
	void testCreateVideoSuccess() throws Exception {
		VideoDTO dto = mockVideoDTO();
		Video video = mockVideo(1L);
		given(videoService.createVideo(any(VideoDTO.class))).willReturn(video);

		mockMvc.perform(
				post(ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.title").value("Mock Video"));
	}

	@Test
	@DisplayName("POST / - validation fail (thiếu title)")
	void testCreateVideoValidationFail() throws Exception {
		VideoDTO dto = mockVideoDTO();
		dto.setTitle(""); // invalid

		mockMvc.perform(
				post(ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());
	}

	// ----------------------------
	// PUT /{id}
	// ----------------------------
	@Test
	@DisplayName("PUT /{id} - cập nhật thành công")
	void testUpdateVideoSuccess() throws Exception {
		VideoDTO dto = mockVideoDTO();
		Video updated = mockVideo(1L);
		given(videoService.updateVideo(eq(1L), any(VideoDTO.class))).willReturn(updated);

		mockMvc.perform(put(ENDPOINT + "/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Mock Video"));
	}

	@Test
	@DisplayName("PUT /{id} - video không tồn tại")
	void testUpdateVideoNotFound() throws Exception {
		given(videoService.updateVideo(eq(1L), any(VideoDTO.class)))
				.willThrow(new BusinessException(404, "Video not found"));

		mockMvc.perform(put(ENDPOINT + "/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockVideoDTO()))).andExpect(status().isNotFound());
	}

	// ----------------------------
	// PATCH /{id}/enabled
	// ----------------------------
	@Test
	@DisplayName("PATCH /{id}/enabled - toggle enabled thành công")
	void testToggleEnabledSuccess() throws Exception {
		mockMvc.perform(patch(ENDPOINT + "/1/enabled")).andExpect(status().isOk());

		then(videoService).should().toggleEnabled(1L);
	}

	@Test
	@DisplayName("PATCH /{id}/enabled - video không tồn tại")
	void testToggleEnabledNotFound() throws Exception {
		doThrow(new BusinessException(404, "Video not found")).when(videoService).toggleEnabled(1L);

		mockMvc.perform(patch(ENDPOINT + "/1/enabled")).andExpect(status().isNotFound());
	}

	// ----------------------------
	// DELETE /{id}
	// ----------------------------
	@Test
	@DisplayName("DELETE /{id} - xóa thành công (204)")
	void testDeleteVideoSuccess() throws Exception {
		mockMvc.perform(delete(ENDPOINT + "/1")).andExpect(status().isNoContent());

		then(videoService).should().deleteVideo(1L);
	}

	@Test
	@DisplayName("DELETE /{id} - video không tồn tại")
	void testDeleteVideoNotFound() throws Exception {
		willThrow(new BusinessException(404, "Video not found")).given(videoService).deleteVideo(1L);

		mockMvc.perform(delete(ENDPOINT + "/1")).andExpect(status().isNotFound());
	}
}
