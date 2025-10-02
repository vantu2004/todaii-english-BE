package com.todaii.english.server.admin;

import com.todaii.english.core.entity.AdminRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback
class AdminRoleRepositoryTests {

	@Autowired
	private AdminRoleRepository roleRepository;

	private AdminRole superAdmin;
	private AdminRole userManager;
	private AdminRole contentManager;

	@BeforeEach
	void setup() {
		// SUPER_ADMIN
		superAdmin = new AdminRole();
		superAdmin.setCode("SUPER_ADMIN");
		superAdmin.setDescription("Highest permission");
		roleRepository.save(superAdmin);

		// USER_MANAGER
		userManager = new AdminRole();
		userManager.setCode("USER_MANAGER");
		userManager.setDescription("Can manage users");
		roleRepository.save(userManager);

		// CONTENT_MANAGER
		contentManager = new AdminRole();
		contentManager.setCode("CONTENT_MANAGER");
		contentManager.setDescription("Can manage content");
		roleRepository.save(contentManager);
	}

	// -------- CREATE --------
	@Test
	void testCreateRole_success() {
		AdminRole role = new AdminRole();
		role.setCode("ANALYST");
		role.setDescription("Can view reports");

		AdminRole saved = roleRepository.save(role);

		assertThat(saved.getCode()).isEqualTo("ANALYST");
		assertThat(saved.getDescription()).isEqualTo("Can view reports");
	}

	// -------- FIND BY ID --------
	@Test
	void testFindById_existing_shouldReturnRole() {
		Optional<AdminRole> found = roleRepository.findById("SUPER_ADMIN");

		assertThat(found).isPresent();
		assertThat(found.get().getDescription()).isEqualTo("Highest permission");
	}

	@Test
	void testFindById_existingContentManager() {
		Optional<AdminRole> found = roleRepository.findById("CONTENT_MANAGER");

		assertThat(found).isPresent();
		assertThat(found.get().getDescription()).isEqualTo("Can manage content");
	}

	@Test
	void testFindById_notExisting_shouldReturnEmpty() {
		Optional<AdminRole> found = roleRepository.findById("NOT_EXIST");

		assertThat(found).isEmpty();
	}

	// -------- FIND ALL --------
	@Test
	void testFindAll_shouldReturnAllRoles() {
		List<AdminRole> roles = roleRepository.findAll();

		assertThat(roles).extracting(AdminRole::getCode).contains("SUPER_ADMIN", "USER_MANAGER", "CONTENT_MANAGER");
	}

	// -------- EXISTS --------
	@Test
	void testExistsById_shouldReturnTrueIfExists() {
		boolean exists = roleRepository.existsById("USER_MANAGER");

		assertThat(exists).isTrue();
	}

	@Test
	void testExistsById_shouldReturnFalseIfNotExists() {
		boolean exists = roleRepository.existsById("NOT_EXIST");

		assertThat(exists).isFalse();
	}

}
