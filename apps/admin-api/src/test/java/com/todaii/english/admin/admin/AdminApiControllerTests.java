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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaii.english.admin.security.TestSecurityConfig;
import com.todaii.english.core.admin.admin.Admin;
import com.todaii.english.core.admin.admin.AdminRole;
import com.todaii.english.core.admin.admin.AdminService;
import com.todaii.english.shared.enums.AdminStatus;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.enums.error_code.AdminErrorCode;
import com.todaii.english.shared.request.admin.CreateAdminRequest;

@WebMvcTest(controllers = AdminApiController.class)
@Import(TestSecurityConfig.class)
public class AdminApiControllerTests {

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
	@DisplayName("GET /api/v1/admin - should return 200 and list of admins")
	void testGetAllAdmins_success() throws Exception {
		List<Admin> admins = Arrays.asList(sampleAdmin(1L), sampleAdmin(2L));

		given(this.adminService.findAll()).willReturn(admins);

		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].email").value("admin1@test.com"))
				.andExpect(jsonPath("$[1].email").value("admin2@test.com"));
	}

	@Test
	@DisplayName("GET /api/v1/admin - should return 204 when no admins")
	void testGetAllAdmins_noContent() throws Exception {
		given(this.adminService.findAll()).willReturn(Collections.emptyList());

		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNoContent());
	}

	// ---------- GET BY ID ----------
	@Test
	@DisplayName("GET /api/v1/admin/{id} - should return 200 and admin details")
	void testGetAdminById_success() throws Exception {
		Admin admin = sampleAdmin(1L);
		given(this.adminService.findById(1L)).willReturn(admin);

		mockMvc.perform(get(END_POINT_PATH + "/{id}", 1L)).andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("admin1@test.com"))
				.andExpect(jsonPath("$.displayName").value("Admin 1"));
	}

	@Test
	@DisplayName("GET /api/v1/admin/{id} - should return 404 if admin not found")
	void testGetAdminById_notFound() throws Exception {
		given(this.adminService.findById(99L)).willThrow(new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

		mockMvc.perform(get(END_POINT_PATH + "/{id}", 99L)).andExpect(status().isNotFound());
	}

	// ---------- CREATE ----------
	@Test
	@DisplayName("POST /api/v1/admin - should create admin successfully")
	void testCreateAdmin_success() throws Exception {
		CreateAdminRequest request = new CreateAdminRequest();
		request.setEmail("newadmin@test.com");
		request.setPassword("password123");
		request.setDisplayName("New Admin");
		request.setRoleCodes(Collections.singleton("SUPER_ADMIN"));

		Admin admin = new Admin();
		admin.setEmail(request.getEmail());
		admin.setDisplayName(request.getDisplayName());
		admin.setPasswordHash(request.getPassword());

		AdminRole adminRole = AdminRole.builder().code("SUPER_ADMIN").description("Super admin").build();
		admin.setRoles(Set.of(adminRole));

		given(this.adminService.create(any(CreateAdminRequest.class))).willReturn(admin);

		mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("newadmin@test.com"))
				.andExpect(jsonPath("$.displayName").value("New Admin"));
	}

	@Test
	@DisplayName("POST /api/v1/admin - should return 409 if email already exists")
	void testCreateAdmin_emailExists() throws Exception {
		CreateAdminRequest request = new CreateAdminRequest();
		request.setEmail("duplicate@test.com");
		request.setPassword("password123");
		request.setDisplayName("Dup Admin");
		request.setRoleCodes(Collections.singleton("SUPER_ADMIN"));

		given(this.adminService.create(any(CreateAdminRequest.class)))
				.willThrow(new BusinessException(AdminErrorCode.ADMIN_ALREADY_EXISTS));

		mockMvc.perform(post(END_POINT_PATH).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isConflict());
	}

	// ---------- DELETE ----------
	@Test
	@DisplayName("DELETE /api/v1/admin/{id} - should delete admin successfully")
	void testDeleteAdmin_success() throws Exception {
		doNothing().when(this.adminService).delete(1L);

		mockMvc.perform(delete(END_POINT_PATH + "/{id}", 1L)).andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("DELETE /api/v1/admin/{id} - should return 404 if admin not found")
	void testDeleteAdmin_notFound() throws Exception {
		doThrow(new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND)).when(this.adminService).delete(99L);

		mockMvc.perform(delete(END_POINT_PATH + "/{id}", 99L)).andExpect(status().isNotFound());
	}
}
