package com.todaii.english.server.topic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.Topic;
import com.todaii.english.shared.enums.TopicType;
import com.todaii.english.shared.enums.error_code.CommonErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.CreateTopicRequest;
import com.todaii.english.server.security.TestSecurityConfig;

@WebMvcTest(controllers = TopicApiController.class)
@Import(TestSecurityConfig.class)
class TopicApiControllerTests {

	private static final String END_POINT_PATH = "/api/v1/topic";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TopicService topicService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("GET /topic trả về danh sách khi có dữ liệu")
	void testGetAll_ok() throws Exception {
		Topic t1 = Topic.builder().id(1L).name("News").alias("news").build();
		Topic t2 = Topic.builder().id(2L).name("Sports").alias("sports").build();

		given(topicService.findAll()).willReturn(Arrays.asList(t1, t2));

		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("News"));
	}

	@Test
	@DisplayName("GET /topic trả về 204 khi rỗng")
	void testGetAll_noContent() throws Exception {
		given(topicService.findAll()).willReturn(Collections.emptyList());

		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("GET /topic/{id} thành công")
	void testGetById_ok() throws Exception {
		Topic t1 = Topic.builder().id(1L).name("News").alias("news").build();

		given(topicService.findById(1L)).willReturn(t1);

		mockMvc.perform(get(END_POINT_PATH + "/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.alias").value("news"));
	}

	@Test
	@DisplayName("GET /topic/{id} không tìm thấy")
	void testGetById_notFound() throws Exception {
		given(topicService.findById(anyLong())).willThrow(new BusinessException(CommonErrorCode.NOT_FOUND));

		mockMvc.perform(get(END_POINT_PATH + "/99")).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("POST /topic tạo thành công")
	void testCreate_ok() throws Exception {
		CreateTopicRequest request = new CreateTopicRequest();
		request.setName("News");
		request.setTopicType(TopicType.ARTICLE);

		Topic topic = Topic.builder().id(1L).name("News").alias("news").topicType(TopicType.ARTICLE).enabled(true)
				.build();

		given(topicService.create(any(CreateTopicRequest.class))).willReturn(topic);

		mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.alias").value("news")).andExpect(jsonPath("$.topicType").value("ARTICLE"));
	}

	@Test
	@DisplayName("POST /topic alias trùng")
	void testCreate_conflict() throws Exception {
		CreateTopicRequest request = new CreateTopicRequest();
		request.setName("News");
		request.setTopicType(TopicType.ARTICLE);

		given(topicService.create(any(CreateTopicRequest.class))).willThrow(new BusinessException(409, "Alias exists"));

		mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict());
	}

	@Test
	@DisplayName("POST /topic validation lỗi")
	void testCreate_validationError() throws Exception {
		CreateTopicRequest invalidRequest = new CreateTopicRequest();
		invalidRequest.setName(""); // @NotBlank vi phạm
		invalidRequest.setTopicType(TopicType.ARTICLE);

		mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("PUT /topic/{id} update thành công")
	void testUpdate_ok() throws Exception {
		Topic updated = Topic.builder().id(1L).name("Tech").alias("tech").build();
		given(topicService.update(1L, "Tech")).willReturn(updated);

		mockMvc.perform(put(END_POINT_PATH + "/1").param("name", "Tech")).andExpect(status().isOk())
				.andExpect(jsonPath("$.alias").value("tech"));
	}

	@Test
	@DisplayName("PUT /topic/{id} alias trùng")
	void testUpdate_conflict() throws Exception {
		given(topicService.update(anyLong(), anyString())).willThrow(new BusinessException(409, "Alias exists"));

		mockMvc.perform(put(END_POINT_PATH + "/1").param("name", "Sports")).andExpect(status().isConflict());
	}

	@Test
	@DisplayName("DELETE /topic/{id} thành công")
	void testDelete_ok() throws Exception {
		doNothing().when(topicService).softDelete(1L);

		mockMvc.perform(delete(END_POINT_PATH + "/1")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("DELETE /topic/{id} không tìm thấy")
	void testDelete_notFound() throws Exception {
		doThrow(new BusinessException(CommonErrorCode.NOT_FOUND)).when(topicService).softDelete(anyLong());

		mockMvc.perform(delete(END_POINT_PATH + "/99")).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("PATCH /topic/{id}/toggle thành công")
	void testToggle_ok() throws Exception {
		doNothing().when(topicService).toggleEnabled(1L);

		mockMvc.perform(patch(END_POINT_PATH + "/1/enabled")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("PATCH /topic/{id}/toggle không tìm thấy")
	void testToggle_notFound() throws Exception {
		doThrow(new BusinessException(CommonErrorCode.NOT_FOUND)).when(topicService).toggleEnabled(anyLong());

		mockMvc.perform(patch(END_POINT_PATH + "/99/toggle")).andExpect(status().isNotFound());
	}
}
