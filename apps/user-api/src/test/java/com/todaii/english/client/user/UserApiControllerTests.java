package com.todaii.english.client.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.client.security.TestSecurityConfig;
import com.todaii.english.core.client.user.UserService;
import com.todaii.english.core.entity.User;
import com.todaii.english.infra.security.client.CustomUserDetails;
import com.todaii.english.shared.enums.UserStatus;
import com.todaii.english.shared.enums.error_code.AuthErrorCode;
import com.todaii.english.shared.enums.error_code.UserErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.UpdateProfileRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserApiController.class)
@Import(TestSecurityConfig.class)
class UserApiControllerTests {

	private static final String ENDPOINT = "/api/v1/user/me";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	private User user1;

	@BeforeEach
	void setup() {
		user1 = User.builder().id(1L).email("test@example.com").displayName("Test User").enabled(true)
				.status(UserStatus.ACTIVE).build();
	}

	// ===== Helper method tạo Authentication giả =====
	private Authentication authUser(User user) {
		CustomUserDetails principal = new CustomUserDetails(user);
		return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
	}

	// ------------------------------------------------------------------
	// GET /me
	// ------------------------------------------------------------------

	@Test
	@DisplayName("TC-GET-ME-001: Token hợp lệ -> 200 OK + JSON")
	void getProfile_success() throws Exception {
		when(userService.getUserById(1L)).thenReturn(user1);

		mockMvc.perform(get(ENDPOINT).with(authentication(authUser(user1)))).andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("test@example.com"))
				.andExpect(jsonPath("$.displayName").value("Test User"));
	}

	@Test
	@DisplayName("TC-GET-ME-002: User không tồn tại -> 404 USER_NOT_FOUND")
	void getProfile_notFound() throws Exception {
		when(userService.getUserById(1L)).thenThrow(new BusinessException(UserErrorCode.USER_NOT_FOUND));

		mockMvc.perform(get(ENDPOINT).with(authentication(authUser(user1)))).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("TC-GET-ME-003: Không có authentication -> 401 Unauthorized")
	void getProfile_unauthorized() throws Exception {
		mockMvc.perform(get(ENDPOINT)).andExpect(status().isUnauthorized());
	}

	// ------------------------------------------------------------------
	// PUT /me
	// ------------------------------------------------------------------

	@Test
	@DisplayName("TC-PUT-ME-001: Update profile thành công -> 200 OK")
	void updateProfile_success() throws Exception {
		UpdateProfileRequest req = UpdateProfileRequest.builder().displayName("New Name").build();

		User updatedUser = User.builder().displayName("New Name").build();

		when(userService.updateProfile(eq(1L), any(UpdateProfileRequest.class))).thenReturn(updatedUser);

		mockMvc.perform(put(ENDPOINT).with(authentication(authUser(user1))).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isOk())
				.andExpect(jsonPath("$.displayName").value("New Name"));
	}

	@Test
	@DisplayName("TC-PUT-ME-002: User không tồn tại -> 404 USER_NOT_FOUND")
	void updateProfile_notFound() throws Exception {
		UpdateProfileRequest req = UpdateProfileRequest.builder().displayName("New Name").build();

		when(userService.updateProfile(eq(1L), any(UpdateProfileRequest.class)))
				.thenThrow(new BusinessException(UserErrorCode.USER_NOT_FOUND));

		mockMvc.perform(put(ENDPOINT).with(authentication(authUser(user1))).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("TC-PUT-ME-003: Password mới quá ngắn -> 400 PASSWORD_INVALID_LENGTH")
	void updateProfile_passwordTooShort() throws Exception {
		UpdateProfileRequest req = UpdateProfileRequest.builder().oldPassword("123456").newPassword("123").build();

		when(userService.updateProfile(eq(1L), any(UpdateProfileRequest.class)))
				.thenThrow(new BusinessException(AuthErrorCode.PASSWORD_INVALID_LENGTH));

		mockMvc.perform(put(ENDPOINT).with(authentication(authUser(user1))).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("TC-PUT-ME-004: Old password sai -> 400 PASSWORD_INCORRECT")
	void updateProfile_passwordIncorrect() throws Exception {
		UpdateProfileRequest req = UpdateProfileRequest.builder().oldPassword("wrong").newPassword("1234567").build();

		when(userService.updateProfile(eq(1L), any(UpdateProfileRequest.class)))
				.thenThrow(new BusinessException(AuthErrorCode.PASSWORD_INCORRECT));

		mockMvc.perform(put(ENDPOINT).with(authentication(authUser(user1))).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("TC-PUT-ME-005: Body request invalid -> 400 Bad Request")
	void updateProfile_badRequest_invalidBody() throws Exception {
		UpdateProfileRequest req = UpdateProfileRequest.builder().oldPassword("wrong").newPassword("1234567").build();

		mockMvc.perform(put(ENDPOINT).with(authentication(authUser(user1))).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest());
	}
}
