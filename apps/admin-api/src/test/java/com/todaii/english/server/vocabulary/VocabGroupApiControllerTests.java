package com.todaii.english.server.vocabulary;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.todaii.english.core.entity.VocabGroup;
import com.todaii.english.server.security.TestSecurityConfig;
import com.todaii.english.shared.exceptions.BusinessException;

@WebMvcTest(VocabGroupApiController.class)
@Import(TestSecurityConfig.class)
class VocabGroupApiControllerTests {

	private static final String ENDPOINT = "/api/v1/vocab-group";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private VocabGroupService vocabGroupService;

	// ==================== GET /api/v1/vocab-group ====================

	@Deprecated
	@Test
	@DisplayName("GET all - trả về 200 OK khi có dữ liệu")
	void testGetAll_ReturnsList() throws Exception {
		List<VocabGroup> list = List.of(VocabGroup.builder().id(1L).name("Common Words").alias("common-words").build(),
				VocabGroup.builder().id(2L).name("Business English").alias("business-english").build());

		when(vocabGroupService.findAll()).thenReturn(list);

		mockMvc.perform(get(ENDPOINT)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].name").value("Common Words"));
	}

	@Deprecated
	@Test
	@DisplayName("GET all - trả về 204 No Content khi không có dữ liệu")
	void testGetAll_EmptyList() throws Exception {
		when(vocabGroupService.findAll()).thenReturn(Collections.emptyList());

		mockMvc.perform(get(ENDPOINT)).andExpect(status().isNoContent());
	}

	// ==================== GET /api/v1/vocab-group (Paged) ====================

	@Test
	@DisplayName("GET all paged - trả về 200 OK khi có dữ liệu")
	void testGetAllPaged_ReturnsList() throws Exception {
		List<VocabGroup> list = List.of(VocabGroup.builder().id(1L).name("Common Words").alias("common-words").build(),
				VocabGroup.builder().id(2L).name("Business English").alias("business-english").build());

		Page<VocabGroup> page = new PageImpl<>(list);

		when(vocabGroupService.findAllPaged(1, 10, "id", "desc", null)).thenReturn(page);

		mockMvc.perform(get(ENDPOINT))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content[0].name").value("Common Words")).andExpect(jsonPath("$.page").value(1))
				.andExpect(jsonPath("$.size").value(10)) // Kích thước thực tế của content list
				.andExpect(jsonPath("$.totalElements").value(2)).andExpect(jsonPath("$.totalPages").value(1));
	}

	@Test
	@DisplayName("GET all paged - trả về 200 OK với content rỗng khi không có dữ liệu")
	void testGetAllPaged_EmptyList() throws Exception {
		Page<VocabGroup> emptyPage = Page.empty();

		when(vocabGroupService.findAllPaged(1, 10, "id", "desc", null)).thenReturn(emptyPage);

		mockMvc.perform(get(ENDPOINT + "?page=1&size=10")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isEmpty()).andExpect(jsonPath("$.totalElements").value(0));
	}

	@Test
	@DisplayName("GET all paged - trả về 400 Bad Request khi page < 1")
	void testGetAllPaged_InvalidPage() throws Exception {
		mockMvc.perform(get(ENDPOINT + "?page=0")).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("GET all paged - trả về 400 Bad Request khi size < 1")
	void testGetAllPaged_InvalidSize() throws Exception {
		mockMvc.perform(get(ENDPOINT + "?size=0")).andExpect(status().isBadRequest());
	}

	// ==================== GET /api/v1/vocab-group/{id} ====================

	@Test
	@DisplayName("GET by id - trả về 200 OK khi tìm thấy")
	void testGetById_Success() throws Exception {
		VocabGroup group = VocabGroup.builder().id(1L).name("Common Words").alias("common-words").build();

		when(vocabGroupService.findById(1L)).thenReturn(group);

		mockMvc.perform(get(ENDPOINT + "/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.alias").value("common-words"));
	}

	@Test
	@DisplayName("GET by id - trả về 404 khi không tìm thấy")
	void testGetById_NotFound() throws Exception {
		when(vocabGroupService.findById(99L)).thenThrow(new BusinessException(404, "Vocabulary group not found"));

		mockMvc.perform(get(ENDPOINT + "/99")).andExpect(status().isNotFound());
	}

	// ==================== POST /api/v1/vocab-group ====================

	@Test
	@DisplayName("POST create - thành công với dữ liệu hợp lệ")
	void testCreate_Success() throws Exception {
		VocabGroup saved = VocabGroup.builder().id(1L).name("Common Words").alias("common-words").build();

		when(vocabGroupService.create(anyString())).thenReturn(saved);

		mockMvc.perform(post(ENDPOINT).contentType(MediaType.APPLICATION_FORM_URLENCODED).param("name", "Common Words"))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.alias").value("common-words"));
	}

	@Test
	@DisplayName("POST create - 409 khi alias đã tồn tại")
	void testCreate_ConflictAlias() throws Exception {
		when(vocabGroupService.create(anyString()))
				.thenThrow(new BusinessException(409, "Alias already exists: common-words"));

		mockMvc.perform(post(ENDPOINT).contentType(MediaType.APPLICATION_FORM_URLENCODED).param("name", "Common Words"))
				.andExpect(status().isConflict());
	}

	@Test
	@DisplayName("POST create - 400 khi name trống")
	void testCreate_BlankName() throws Exception {
		mockMvc.perform(post(ENDPOINT).contentType(MediaType.APPLICATION_FORM_URLENCODED).param("name", ""))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST create - 400 khi name quá dài")
	void testCreate_NameTooLong() throws Exception {
		String longName = "a".repeat(192);

		mockMvc.perform(post(ENDPOINT).contentType(MediaType.APPLICATION_FORM_URLENCODED).param("name", longName))
				.andExpect(status().isBadRequest());
	}

	// ==================== PUT /api/v1/vocab-group/{id} ====================

	@Test
	@DisplayName("PUT update - thành công khi hợp lệ")
	void testUpdate_Success() throws Exception {
		VocabGroup updated = VocabGroup.builder().id(1L).name("Updated Name").alias("updated-name").build();

		when(vocabGroupService.update(anyLong(), anyString())).thenReturn(updated);

		mockMvc.perform(
				put(ENDPOINT + "/1").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("name", "Updated Name"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.alias").value("updated-name"));
	}

	@Test
	@DisplayName("PUT update - 400 khi name trống")
	void testUpdate_BlankName() throws Exception {
		mockMvc.perform(put(ENDPOINT + "/1").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("name", ""))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("PUT update - 409 khi alias trùng lặp")
	void testUpdate_Conflict() throws Exception {
		when(vocabGroupService.update(anyLong(), anyString()))
				.thenThrow(new BusinessException(409, "Alias already exists"));

		mockMvc.perform(
				put(ENDPOINT + "/1").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("name", "Duplicate"))
				.andExpect(status().isConflict());
	}

	// ==================== DELETE /api/v1/vocab-group/{id} ====================

	@Test
	@DisplayName("DELETE - thành công")
	void testDelete_Success() throws Exception {
		doNothing().when(vocabGroupService).softDelete(1L);

		mockMvc.perform(delete(ENDPOINT + "/1")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("DELETE - lỗi khi id không tồn tại")
	void testDelete_NotFound() throws Exception {
		doThrow(new BusinessException(404, "Not found")).when(vocabGroupService).softDelete(99L);

		mockMvc.perform(delete(ENDPOINT + "/99")).andExpect(status().isNotFound());
	}

	// ============ PATCH /api/v1/vocab-group/{id}/enabled ============

	@Test
	@DisplayName("PATCH toggle enabled - thành công")
	void testToggleEnabled_Success() throws Exception {
		doNothing().when(vocabGroupService).toggleEnabled(1L);

		mockMvc.perform(patch(ENDPOINT + "/1/enabled")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("PATCH toggle enabled - lỗi khi id không tồn tại")
	void testToggleEnabled_NotFound() throws Exception {
		doThrow(new BusinessException(404, "Not found")).when(vocabGroupService).toggleEnabled(99L);

		mockMvc.perform(patch(ENDPOINT + "/99/enabled")).andExpect(status().isNotFound());
	}
}
