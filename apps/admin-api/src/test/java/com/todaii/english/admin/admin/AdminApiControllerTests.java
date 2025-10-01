package com.todaii.english.admin.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.admin.security.TestSecurityConfig;
import com.todaii.english.core.admin.admin.Admin;
import com.todaii.english.core.admin.admin.AdminRole;
import com.todaii.english.core.admin.admin.AdminService;
import com.todaii.english.infra.security.jwt.JwtTokenFilter;
import com.todaii.english.shared.enums.AdminStatus;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.enums.error_code.AdminErrorCode;
import com.todaii.english.shared.request.admin.CreateAdminRequest;
import com.todaii.english.shared.request.admin.UpdateAdminRequest;

@WebMvcTest(controllers = AdminApiController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtTokenFilter.class))
@Import(TestSecurityConfig.class)
class AdminApiControllerTests {

	private static final String END_POINT_PATH = "/api/v1/admin";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private AdminService adminService;

	private Admin sampleAdmin(Long id) {
		return Admin.builder().id(id).email("admin" + id + "@test.com").displayName("Admin " + id)
				.status(AdminStatus.PENDING).build();
	}

	// ---------- GET ALL ----------
	@Test
	@DisplayName("GET /api/v1/admin - 200 OK với danh sách admins")
	void testGetAllAdmins_success() throws Exception {
		List<Admin> admins = Arrays.asList(sampleAdmin(1L), sampleAdmin(2L));
		given(adminService.findAll()).willReturn(admins);

		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].email").value("admin1@test.com"))
				.andExpect(jsonPath("$[1].email").value("admin2@test.com"));
	}

	@Test
	@DisplayName("GET /api/v1/admin - 204 No Content khi rỗng")
	void testGetAllAdmins_noContent() throws Exception {
		given(adminService.findAll()).willReturn(Collections.emptyList());

		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNoContent());
	}

	// ---------- GET BY ID ----------
	@Test
	@DisplayName("GET /api/v1/admin/{id} - 200 OK trả về admin")
	void testGetAdminById_success() throws Exception {
		Admin admin = sampleAdmin(1L);
		given(adminService.findById(1L)).willReturn(admin);

		mockMvc.perform(get(END_POINT_PATH + "/{id}", 1L)).andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("admin1@test.com"));
	}

	@Test
	@DisplayName("GET /api/v1/admin/{id} - 404 Not Found nếu không tồn tại")
	void testGetAdminById_notFound() throws Exception {
		given(adminService.findById(99L)).willThrow(new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

		mockMvc.perform(get(END_POINT_PATH + "/{id}", 99L)).andExpect(status().isNotFound());
	}

	// ---------- GET /me ----------
//	@Test
//	@DisplayName("GET /api/v1/admin/me - 200 OK trả về profile của admin hiện tại")
//	@WithMockUser(username = "admin1@test.com", roles = {"SUPER_ADMIN"})
//	void testGetProfile_success() throws Exception {
//	    Admin admin = sampleAdmin(1L);
//	    given(adminService.findById(1L)).willReturn(admin);
//
//	    mockMvc.perform(get(END_POINT_PATH + "/me"))
//	            .andExpect(status().isOk())
//	            .andExpect(jsonPath("$.email").value("admin1@test.com"));
//	}

	// ---------- CREATE ----------
	@Test
	@DisplayName("POST /api/v1/admin - 200 OK khi tạo thành công")
	void testCreateAdmin_success() throws Exception {
		CreateAdminRequest request = new CreateAdminRequest();
		request.setEmail("new@test.com");
		request.setPassword("123456");
		request.setDisplayName("New Admin");
		request.setRoleCodes(Set.of("SUPER_ADMIN"));

		Admin admin = sampleAdmin(10L);
		admin.setEmail(request.getEmail());
		admin.setDisplayName(request.getDisplayName());
		admin.setRoles(Set.of(AdminRole.builder().code("SUPER_ADMIN").build()));

		given(adminService.create(any(CreateAdminRequest.class))).willReturn(admin);

		mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("new@test.com"))
				.andExpect(jsonPath("$.displayName").value("New Admin"));
	}

	@Test
	@DisplayName("POST /api/v1/admin - 409 Conflict nếu email đã tồn tại")
	void testCreateAdmin_conflict() throws Exception {
		CreateAdminRequest request = new CreateAdminRequest();
		request.setEmail("duplicate@test.com");
		request.setPassword("123456");
		request.setDisplayName("Dup Admin");
		request.setRoleCodes(Set.of("SUPER_ADMIN"));

		given(adminService.create(any(CreateAdminRequest.class)))
				.willThrow(new BusinessException(AdminErrorCode.ADMIN_ALREADY_EXISTS));

		mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict());
	}

	// ---------- UPDATE /me ----------
//	@Test
//	@DisplayName("PUT /api/v1/admin/me - 200 OK khi update profile")
//	void testUpdateProfile_success() throws Exception {
//		UpdateProfileRequest request = new UpdateProfileRequest();
//		request.setDisplayName("My Updated");
//		request.setAvatarUrl("https://example.com/me.png");
//
//		Admin updated = sampleAdmin(1L);
//		updated.setDisplayName("My Updated");
//		updated.setAvatarUrl("https://example.com/me.png");
//
//		given(adminService.updateProfile(any(Long.class), any(UpdateProfileRequest.class))).willReturn(updated);
//
//		mockMvc.perform(put(END_POINT_PATH + "/me").contentType(MediaType.APPLICATION_JSON)
//				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
//				.andExpect(jsonPath("$.displayName").value("My Updated"));
//	}

	// ---------- UPDATE ADMIN ----------
	@Test
	@DisplayName("PUT /api/v1/admin/{id} - 200 OK khi update thành công")
	void testUpdateAdmin_success() throws Exception {
		UpdateAdminRequest request = new UpdateAdminRequest();
		request.setDisplayName("Updated");
		request.setAvatarUrl("https://example.com/updated.png");
		request.setRoleCodes(Set.of("ADMIN"));

		Admin updated = sampleAdmin(2L);
		updated.setDisplayName("Updated");

		given(adminService.updateAdmin(any(Long.class), any(UpdateAdminRequest.class))).willReturn(updated);

		mockMvc.perform(put(END_POINT_PATH + "/{id}", 2L).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(jsonPath("$.displayName").value("Updated"));
	}

	@Test
	@DisplayName("PUT /api/v1/admin/{id} - 404 Not Found nếu không tồn tại")
	void testUpdateAdmin_notFound() throws Exception {
		UpdateAdminRequest request = new UpdateAdminRequest();
		request.setDisplayName("Not Found");
		request.setRoleCodes(Set.of("ADMIN"));

		given(adminService.updateAdmin(any(Long.class), any(UpdateAdminRequest.class)))
				.willThrow(new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

		mockMvc.perform(put(END_POINT_PATH + "/{id}", 99L).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isNotFound());
	}

	// ---------- TOGGLE ENABLE ----------
	@Test
	@DisplayName("PUT /api/v1/admin/toggle-enabled/{id} - 200 OK khi toggle thành công")
	void testToggleEnabled_success() throws Exception {
		doNothing().when(adminService).toggleEnabled(1L);

		mockMvc.perform(put(END_POINT_PATH + "/toggle-enabled/{id}", 1L)).andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT /api/v1/admin/toggle-enabled/{id} - 404 nếu admin không tồn tại")
	void testToggleEnabled_notFound() throws Exception {
		doThrow(new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND)).when(adminService).toggleEnabled(99L);

		mockMvc.perform(put(END_POINT_PATH + "/toggle-enabled/{id}", 99L)).andExpect(status().isNotFound());
	}

	// ---------- DELETE ----------
	@Test
	@DisplayName("DELETE /api/v1/admin/{id} - 204 No Content khi xoá thành công")
	void testDeleteAdmin_success() throws Exception {
		doNothing().when(adminService).delete(1L);

		mockMvc.perform(delete(END_POINT_PATH + "/{id}", 1L)).andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("DELETE /api/v1/admin/{id} - 404 nếu không tồn tại")
	void testDeleteAdmin_notFound() throws Exception {
		doThrow(new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND)).when(adminService).delete(99L);

		mockMvc.perform(delete(END_POINT_PATH + "/{id}", 99L)).andExpect(status().isNotFound());
	}
}
