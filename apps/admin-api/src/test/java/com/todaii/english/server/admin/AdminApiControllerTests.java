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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
		return new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
	}

	// ------------------------------------------------------------------
	// 1. GET /api/v1/admin (Paged)
	// ------------------------------------------------------------------
	@Test
	void getAllAdminsPaged_success() throws Exception {
		Page<Admin> page = new PageImpl<>(List.of(admin1));
		when(adminService.findAllPaged(1, 10, "id", "desc", null)).thenReturn(page);

		mockMvc.perform(get(BASE_ENDPOINT)).andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].email", is("admin1@test.com")));
	}

	@Test
	void getAllAdminsPaged_empty() throws Exception {
		Page<Admin> page = new PageImpl<>(Collections.emptyList());
		when(adminService.findAllPaged(1, 10, "id", "desc", null)).thenReturn(page);

		mockMvc.perform(get(BASE_ENDPOINT)).andExpect(status().isOk()).andExpect(jsonPath("$.content").isEmpty());
	}

	// ------------------------------------------------------------------
	// 2. GET /api/v1/admin/{id}
	// ------------------------------------------------------------------
	@Test
	void getAdminById_success() throws Exception {
		when(adminService.findById(1L)).thenReturn(admin1);

		mockMvc.perform(get(BASE_ENDPOINT + "/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is("admin1@test.com")));
	}

	@Test
	void getAdminById_notFound() throws Exception {
		when(adminService.findById(9999L)).thenThrow(new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

		mockMvc.perform(get(BASE_ENDPOINT + "/9999")).andExpect(status().isNotFound());
	}

	// ------------------------------------------------------------------
	// 3. GET /api/v1/admin/me
	// ------------------------------------------------------------------
	@Test
	void getProfile_success() throws Exception {
		when(adminService.findById(1L)).thenReturn(admin1);

		mockMvc.perform(get(BASE_ENDPOINT + "/me").with(authentication(auth(admin1)))).andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is("admin1@test.com")));
	}

	@Test
	void getProfile_unauthorized() throws Exception {
		mockMvc.perform(get(BASE_ENDPOINT + "/me")).andExpect(status().isUnauthorized());
	}

	// ------------------------------------------------------------------
	// 4. POST /api/v1/admin
	// ------------------------------------------------------------------
	@Test
	void createAdmin_success() throws Exception {
		CreateAdminRequest req = CreateAdminRequest.builder().email("new@test.com").displayName("New Admin")
				.password("123456").roleCodes(Set.of("SUPER_ADMIN")).build();

		when(adminService.create(any(CreateAdminRequest.class))).thenReturn(admin1);

		mockMvc.perform(post(BASE_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.email", is("admin1@test.com")));
	}

	@Test
	void createAdmin_alreadyExists() throws Exception {
		CreateAdminRequest req = CreateAdminRequest.builder().email("exist@test.com").displayName("Dup Admin")
				.password("123456").roleCodes(Set.of("SUPER_ADMIN")).build();

		when(adminService.create(any(CreateAdminRequest.class)))
				.thenThrow(new BusinessException(AdminErrorCode.ADMIN_ALREADY_EXISTS));

		mockMvc.perform(post(BASE_ENDPOINT).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req))).andExpect(status().isConflict());
	}

	// ------------------------------------------------------------------
	// 5. PUT /api/v1/admin/me
	// ------------------------------------------------------------------
	@Test
	void updateProfile_success() throws Exception {
		UpdateProfileRequest req = UpdateProfileRequest.builder().displayName("Le Van Tu").build();

		when(adminService.updateProfile(eq(1L), any(UpdateProfileRequest.class))).thenReturn(admin1);

		mockMvc.perform(put(BASE_ENDPOINT + "/me").with(authentication(auth(admin1)))
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isOk());
	}

	@Test
	void updateProfile_incorrectPassword() throws Exception {
		UpdateProfileRequest req = UpdateProfileRequest.builder().displayName("Le Van Tu").oldPassword("wrong")
				.newPassword("1234567").build();

		when(adminService.updateProfile(eq(1L), any(UpdateProfileRequest.class)))
				.thenThrow(new BusinessException(AuthErrorCode.PASSWORD_INCORRECT));

		mockMvc.perform(put(BASE_ENDPOINT + "/me").with(authentication(auth(admin1)))
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isBadRequest());
	}

	// ------------------------------------------------------------------
	// 6. DELETE /api/v1/admin/{id}
	// ------------------------------------------------------------------
	@Test
	void deleteAdmin_success() throws Exception {
		doNothing().when(adminService).delete(1L);

		mockMvc.perform(delete(BASE_ENDPOINT + "/1")).andExpect(status().isNoContent());
	}

	@Test
	void deleteAdmin_notFound() throws Exception {
		Mockito.doThrow(new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND)).when(adminService).delete(9999L);

		mockMvc.perform(delete(BASE_ENDPOINT + "/9999")).andExpect(status().isNotFound());
	}
}
