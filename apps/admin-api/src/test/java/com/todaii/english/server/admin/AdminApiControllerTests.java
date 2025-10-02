package com.todaii.english.server.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.core.entity.Admin;
import com.todaii.english.core.entity.AdminRole;
import com.todaii.english.server.security.CustomAdminDetails;
import com.todaii.english.server.security.TestSecurityConfig;
import com.todaii.english.shared.enums.AdminStatus;
import com.todaii.english.shared.enums.error_code.AdminErrorCode;
import com.todaii.english.shared.enums.error_code.AuthErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.UpdateProfileRequest;
import com.todaii.english.shared.request.server.CreateAdminRequest;
import com.todaii.english.shared.request.server.UpdateAdminRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminApiController.class)
@Import(TestSecurityConfig.class)
class AdminApiControllerTests {

	private static final String BASE_ENDPOINT = "/api/v1/admin";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AdminService adminService;

	@Autowired
	private ObjectMapper objectMapper;

	private Admin admin1;

	@BeforeEach
	void setup() {
		AdminRole role = AdminRole.builder().code("SUPER_ADMIN").description("Super admin").build();
		admin1 = Admin.builder().id(1L).email("admin1@test.com").displayName("Admin One").enabled(true)
				.status(AdminStatus.ACTIVE).roles(Set.of(role)).build();
	}

	private Authentication auth(Admin admin) {
		CustomAdminDetails details = new CustomAdminDetails(admin);
		return new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities() // dùng quyền thật sự
		);
	}

	// ------------------------------------------------------------------
	// 1. GET /api/v1/admin
	// ------------------------------------------------------------------
	@Test
	@DisplayName("TC-GET-001: Có admin trong DB -> 200 OK")
	void getAllAdmins_success() throws Exception {
		when(adminService.findAll()).thenReturn(List.of(admin1));

		mockMvc.perform(get(BASE_ENDPOINT)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].email", is("admin1@test.com")));
	}

	@Test
	@DisplayName("TC-GET-002: Không có admin nào -> 204 No Content")
	void getAllAdmins_noContent() throws Exception {
		when(adminService.findAll()).thenReturn(Collections.emptyList());

		mockMvc.perform(get(BASE_ENDPOINT)).andExpect(status().isNoContent());
	}

	// ------------------------------------------------------------------
	// 2. GET /api/v1/admin/{id}
	// ------------------------------------------------------------------
	@Test
	@DisplayName("TC-GET-ID-001: Admin tồn tại -> 200 OK")
	void getAdminById_success() throws Exception {
		when(adminService.findById(1L)).thenReturn(admin1);

		mockMvc.perform(get(BASE_ENDPOINT + "/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is("admin1@test.com")));
	}

	@Test
	@DisplayName("TC-GET-ID-002: Admin không tồn tại -> 400 ADMIN_NOT_FOUND")
	void getAdminById_notFound() throws Exception {
		when(adminService.findById(9999L)).thenThrow(new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

		mockMvc.perform(get(BASE_ENDPOINT + "/9999")).andExpect(status().isNotFound());
	}

	// ------------------------------------------------------------------
	// 3. GET /api/v1/admin/me
	// ------------------------------------------------------------------
	@Test
	@DisplayName("TC-GET-ME-001: Token hợp lệ -> trả về admin hiện tại")
	void getProfile_success() throws Exception {
		when(adminService.findById(1L)).thenReturn(admin1);

		mockMvc.perform(get(BASE_ENDPOINT + "/me").with(authentication(auth(admin1)))).andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is("admin1@test.com")));
	}

	// đáng lẽ
	@Test
	@DisplayName("TC-GET-ME-002: Không có authentication -> 401 Unauthorized")
	void getProfile_unauthorized() throws Exception {
		mockMvc.perform(get(BASE_ENDPOINT + "/me")).andExpect(status().isUnauthorized());
	}

	// ------------------------------------------------------------------
	// 4. POST /api/v1/admin
	// ------------------------------------------------------------------
	@Test
	@DisplayName("TC-POST-001: Tạo thành công -> 200 OK")
	void createAdmin_success() throws Exception {
		CreateAdminRequest req = CreateAdminRequest.builder().email("new@test.com").displayName("New Admin")
				.password("123456").roleCodes(Set.of("SUPER_ADMIN")).build();

		when(adminService.create(any(CreateAdminRequest.class))).thenReturn(admin1);

		mockMvc.perform(post(BASE_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is("admin1@test.com")));
	}

	@Test
	@DisplayName("TC-POST-002: Email đã tồn tại -> 409 ADMIN_ALREADY_EXISTS")
	void createAdmin_alreadyExists() throws Exception {
		CreateAdminRequest req = CreateAdminRequest.builder().email("new@test.com").displayName("New Admin")
				.password("123456").roleCodes(Set.of("SUPER_ADMIN")).build();

		when(adminService.create(any(CreateAdminRequest.class)))
				.thenThrow(new BusinessException(AdminErrorCode.ADMIN_ALREADY_EXISTS));

		mockMvc.perform(post(BASE_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isConflict());
	}

	@Test
	@DisplayName("TC-POST-003: Thiếu roleCodes -> 400 validation error")
	void createAdmin_missingRoleCodes() throws Exception {
		CreateAdminRequest req = CreateAdminRequest.builder().email("new@test.com").displayName("New Admin")
				.password("123456").roleCodes(Collections.emptySet()).build();

		mockMvc.perform(post(BASE_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("TC-POST-004: Password quá ngắn -> 400 validation error")
	void createAdmin_passwordTooShort() throws Exception {
		CreateAdminRequest req = CreateAdminRequest.builder().email("new@test.com").displayName("New Admin")
				.password("123").roleCodes(Set.of("SUPER_ADMIN")).build();

		mockMvc.perform(post(BASE_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest());
	}

	// ------------------------------------------------------------------
	// 5. PUT /api/v1/admin/me
	// ------------------------------------------------------------------
	@Test
	@DisplayName("TC-PUT-ME-001: Update thành công -> 200 OK")
	void updateProfile_success() throws Exception {
		UpdateProfileRequest req = UpdateProfileRequest.builder().displayName("Le Van Tu").build();

		when(adminService.updateProfile(eq(1L), any(UpdateProfileRequest.class))).thenReturn(admin1);

		mockMvc.perform(put(BASE_ENDPOINT + "/me").with(authentication(auth(admin1)))
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("TC-PUT-ME-002: Đổi password thành công -> 200 OK")
	void updateProfile_changePasswordSuccess() throws Exception {
		UpdateProfileRequest req = UpdateProfileRequest.builder().displayName("Le Van Tu").oldPassword("123456")
				.newPassword("1234567").build();

		when(adminService.updateProfile(eq(1L), any(UpdateProfileRequest.class))).thenReturn(admin1);

		mockMvc.perform(put(BASE_ENDPOINT + "/me").with(authentication(auth(admin1)))
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("TC-PUT-ME-003: OldPassword sai -> 400 PASSWORD_INCORRECT")
	void updateProfile_incorrectPassword() throws Exception {
		UpdateProfileRequest req = UpdateProfileRequest.builder().displayName("Le Van Tu").oldPassword("123456")
				.newPassword("1234567").build();

		when(adminService.updateProfile(eq(1L), any(UpdateProfileRequest.class)))
				.thenThrow(new BusinessException(AuthErrorCode.PASSWORD_INCORRECT));

		mockMvc.perform(put(BASE_ENDPOINT + "/me").with(authentication(auth(admin1)))
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("TC-PUT-ME-004: NewPassword quá ngắn -> 400 PASSWORD_INVALID_LENGTH")
	void updateProfile_passwordTooShort() throws Exception {
		UpdateProfileRequest req = UpdateProfileRequest.builder().displayName("Le Van Tu").oldPassword("123456")
				.newPassword("1234567").build();

		when(adminService.updateProfile(eq(1L), any(UpdateProfileRequest.class)))
				.thenThrow(new BusinessException(AuthErrorCode.PASSWORD_INVALID_LENGTH));

		mockMvc.perform(put(BASE_ENDPOINT + "/me").with(authentication(auth(admin1)))
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("TC-PUT-ME-005: Admin không tồn tại -> 404 ADMIN_NOT_FOUND")
	void updateProfile_adminNotFound() throws Exception {
		UpdateProfileRequest req = UpdateProfileRequest.builder().displayName("Le Van Tu").oldPassword("123456")
				.newPassword("1234567").build();

		when(adminService.updateProfile(eq(1L), any(UpdateProfileRequest.class)))
				.thenThrow(new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

		mockMvc.perform(put(BASE_ENDPOINT + "/me").with(authentication(auth(admin1)))
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isNotFound());
	}

	// ------------------------------------------------------------------
	// 6. PUT /api/v1/admin/{id}
	// ------------------------------------------------------------------
	@Test
	@DisplayName("TC-PUT-ID-001: Update thành công -> 200 OK")
	void updateAdmin_success() throws Exception {
		UpdateAdminRequest req = UpdateAdminRequest.builder().newPassword("1234567").displayName("Le Van Tu")
				.roleCodes(Set.of("SUPER_ADMIN")).build();

		when(adminService.updateAdmin(eq(1L), any(UpdateAdminRequest.class))).thenReturn(admin1);

		mockMvc.perform(put(BASE_ENDPOINT + "/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isOk());
	}

	@Test
	@DisplayName("TC-PUT-ID-002: Admin không tồn tại -> 404 ADMIN_NOT_FOUND")
	void updateAdmin_notFound() throws Exception {
		UpdateAdminRequest req = UpdateAdminRequest.builder().newPassword("1234567").displayName("Le Van Tu")
				.roleCodes(Set.of("SUPER_ADMIN")).build();

		when(adminService.updateAdmin(eq(9999L), any(UpdateAdminRequest.class)))
				.thenThrow(new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

		mockMvc.perform(put(BASE_ENDPOINT + "/9999").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("TC-PUT-ID-003: Update password hợp lệ -> 200 OK")
	void updateAdmin_changePasswordSuccess() throws Exception {
		UpdateAdminRequest req = UpdateAdminRequest.builder().newPassword("1234567").displayName("Le Van Tu")
				.roleCodes(Set.of("SUPER_ADMIN")).build();

		when(adminService.updateAdmin(eq(1L), any(UpdateAdminRequest.class))).thenReturn(admin1);

		mockMvc.perform(put(BASE_ENDPOINT + "/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isOk());
	}

	@Test
	@DisplayName("TC-PUT-ID-004: Password quá ngắn -> 400 PASSWORD_INVALID_LENGTH")
	void updateAdmin_passwordTooShort() throws Exception {
		UpdateAdminRequest req = UpdateAdminRequest.builder().newPassword("1234567").displayName("Le Van Tu")
				.roleCodes(Set.of("SUPER_ADMIN")).build();

		when(adminService.updateAdmin(eq(1L), any(UpdateAdminRequest.class)))
				.thenThrow(new BusinessException(AuthErrorCode.PASSWORD_INVALID_LENGTH));

		mockMvc.perform(put(BASE_ENDPOINT + "/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("TC-PUT-ID-005: Role không tồn tại -> 400 ROLE_NOT_FOUND")
	void updateAdmin_roleNotFound() throws Exception {
		UpdateAdminRequest req = UpdateAdminRequest.builder().newPassword("1234567").displayName("Le Van Tu")
				.roleCodes(Collections.emptySet()).build();

		mockMvc.perform(put(BASE_ENDPOINT + "/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest());
	}

	// ------------------------------------------------------------------
	// 7. PATCH /api/v1/admin/{id}/enabled
	// ------------------------------------------------------------------
	@Test
	@DisplayName("TC-PATCH-001: Admin disabled -> bật enabled -> ACTIVE (ngược lại)")
	void toggleEnabled_fromDisabledToActive() throws Exception {
		// do toggleEnabled là void nên dùng doNothing/doThrow
		doNothing().when(adminService).toggleEnabled(1L);

		mockMvc.perform(patch(BASE_ENDPOINT + "/1/enabled")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("TC-PATCH-002: Admin không tồn tại -> 404 ADMIN_NOT_FOUND")
	void toggleEnabled_notFound() throws Exception {
		Mockito.doThrow(new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND)).when(adminService).toggleEnabled(9999L);

		mockMvc.perform(patch(BASE_ENDPOINT + "/9999/enabled")).andExpect(status().isNotFound());
	}

	// ------------------------------------------------------------------
	// 8. DELETE /api/v1/admin/{id}
	// ------------------------------------------------------------------
	@Test
	@DisplayName("TC-DEL-001: Xóa thành công -> 204 No Content")
	void deleteAdmin_success() throws Exception {
		doNothing().when(adminService).delete(1L);

		mockMvc.perform(delete(BASE_ENDPOINT + "/1")).andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("TC-DEL-002: Admin không tồn tại -> 404 ADMIN_NOT_FOUND")
	void deleteAdmin_notFound() throws Exception {
		Mockito.doThrow(new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND)).when(adminService).delete(9999L);

		mockMvc.perform(delete(BASE_ENDPOINT + "/9999")).andExpect(status().isNotFound());
	}
}
