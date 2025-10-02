package com.todaii.english.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.User;
import com.todaii.english.server.security.TestSecurityConfig;
import com.todaii.english.shared.enums.UserStatus;
import com.todaii.english.shared.enums.error_code.AuthErrorCode;
import com.todaii.english.shared.enums.error_code.UserErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.UpdateUserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserApiController.class)
@Import(TestSecurityConfig.class)
class UserApiControllerTests {

	private static final String ENDPOINT = "/api/v1/user";

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	UserService userService;

	// ------------------------------------------------------------------------
	// GET /user
	// ------------------------------------------------------------------------
	@Test
	@DisplayName("GET /user - trả về 200 OK khi có dữ liệu")
	void getAllUsers_success() throws Exception {
		User user = User.builder().id(1L).email("test@mail.com").displayName("Test").status(UserStatus.ACTIVE).build();
		
		when(userService.findAll()).thenReturn(Arrays.asList(user));

		mockMvc.perform(get(ENDPOINT)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].email").value("test@mail.com"));
	}

	@Test
	@DisplayName("GET /user - trả về 204 No Content khi không có dữ liệu")
	void getAllUsers_noContent() throws Exception {
		when(userService.findAll()).thenReturn(Collections.emptyList());

		mockMvc.perform(get(ENDPOINT)).andExpect(status().isNoContent());
	}

	// ------------------------------------------------------------------------
	// GET /user/{id}
	// ------------------------------------------------------------------------
	@Test
	@DisplayName("GET /user/{id} - trả về 200 OK khi tìm thấy user")
	void getUser_success() throws Exception {
		User user = User.builder().id(1L).email("test@mail.com").displayName("Test").status(UserStatus.ACTIVE).build();
		
		when(userService.findById(1L)).thenReturn(user);

		mockMvc.perform(get(ENDPOINT + "/1")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.email").value("test@mail.com"));
	}

	@Test
	@DisplayName("GET /user/{id} - trả về 404 Not Found khi không tìm thấy user")
	void getUser_notFound() throws Exception {
		when(userService.findById(99L)).thenThrow(new BusinessException(UserErrorCode.USER_NOT_FOUND));

		mockMvc.perform(get(ENDPOINT + "/99")).andExpect(status().isNotFound());
	}

	// ------------------------------------------------------------------------
	// PUT /user/{id}
	// ------------------------------------------------------------------------
	@Test
	@DisplayName("PUT /user/{id} - trả về 200 OK khi update thành công")
	void updateUser_success() throws Exception {
		UpdateUserRequest req = new UpdateUserRequest();
		req.setDisplayName("Updated");
		req.setAvatarUrl("http://avatar.com/a.png");

		User updated = User.builder().id(1L).email("mail@test.com").displayName("Updated").status(UserStatus.ACTIVE)
				.build();
		when(userService.update(eq(1L), any(UpdateUserRequest.class))).thenReturn(updated);

		mockMvc.perform(put(ENDPOINT + "/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.displayName").value("Updated"));
	}

	@Test
	@DisplayName("PUT /user/{id} - trả về 400 Bad Request khi mật khẩu quá ngắn")
	void updateUser_invalidPasswordLength() throws Exception {
		UpdateUserRequest req = new UpdateUserRequest();
		req.setNewPassword("123"); // quá ngắn
		req.setDisplayName("X");

		when(userService.update(eq(1L), any(UpdateUserRequest.class)))
				.thenThrow(new BusinessException(AuthErrorCode.PASSWORD_INVALID_LENGTH));

		mockMvc.perform(put(ENDPOINT + "/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("PUT /user/{id} - trả về 404 Not Found khi user không tồn tại")
	void updateUser_notFound() throws Exception {
		UpdateUserRequest req = new UpdateUserRequest();
		req.setDisplayName("X");

		when(userService.update(eq(99L), any(UpdateUserRequest.class)))
				.thenThrow(new BusinessException(UserErrorCode.USER_NOT_FOUND));

		mockMvc.perform(put(ENDPOINT + "/99").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("PUT /user/{id} - trả về 400 Bad Request khi thiếu displayName")
	void updateUser_missingDisplayName_shouldReturnBadRequest() throws Exception {
		UpdateUserRequest req = new UpdateUserRequest();
		req.setAvatarUrl("http://avatar.com/a.png");

		mockMvc.perform(put(ENDPOINT + "/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("PUT /user/{id} - trả về 400 Bad Request khi displayName rỗng")
	void updateUser_emptyDisplayName_shouldReturnBadRequest() throws Exception {
		UpdateUserRequest req = new UpdateUserRequest();
		req.setDisplayName(""); // invalid vì @Length(min=1)

		mockMvc.perform(put(ENDPOINT + "/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest());
	}

	// ------------------------------------------------------------------------
	// PATCH /user/{id}/enabled
	// ------------------------------------------------------------------------
	@Test
	@DisplayName("PATCH /user/{id}/enabled - trả về 200 OK khi toggle thành công")
	void toggleEnabled_success() throws Exception {
		mockMvc.perform(patch(ENDPOINT + "/1/enabled")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("PATCH /user/{id}/enabled - trả về 404 Not Found khi user không tồn tại")
	void toggleEnabled_notFound() throws Exception {
		doThrow(new BusinessException(UserErrorCode.USER_NOT_FOUND)).when(userService).toggleEnabled(99L);

		mockMvc.perform(patch(ENDPOINT + "/99/enabled")).andExpect(status().isNotFound());
	}

	// ------------------------------------------------------------------------
	// DELETE /user/{id}
	// ------------------------------------------------------------------------
	@Test
	@DisplayName("DELETE /user/{id} - trả về 204 No Content khi xóa thành công")
	void deleteUser_success() throws Exception {
		mockMvc.perform(delete(ENDPOINT + "/1")).andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("DELETE /user/{id} - trả về 404 Not Found khi user không tồn tại")
	void deleteUser_notFound() throws Exception {
		doThrow(new BusinessException(UserErrorCode.USER_NOT_FOUND)).when(userService).delete(99L);

		mockMvc.perform(delete(ENDPOINT + "/99")).andExpect(status().isNotFound());
	}
}
