package com.todaii.english.server.admin;

import com.todaii.english.core.entity.Admin;
import com.todaii.english.core.entity.AdminRole;
import com.todaii.english.shared.enums.AdminStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback
class AdminRepositoryTests {

	@Autowired
	private AdminRepository adminRepository;

	@PersistenceContext
	private EntityManager entityManager;

	private AdminRole superAdminRole;

	@BeforeEach
	void setup() {
		superAdminRole = entityManager.find(AdminRole.class, "SUPER_ADMIN");
	}

	private Admin createAdmin(String email, boolean deleted) {
		Admin admin = Admin.builder().email(email).passwordHash("pass").displayName("test user")
				.status(AdminStatus.PENDING).enabled(true).isDeleted(deleted).build();
		admin.addRole(superAdminRole);
		if (deleted) {
			admin.setIsDeleted(deleted);
			admin.setDeletedAt(LocalDateTime.now());
		}
		return adminRepository.save(admin);
	}

	// -------- CREATE --------
	@Test
	void testCreateAdmin_success() {
		Admin admin = createAdmin("create@test.com", false);

		assertThat(admin.getId()).isNotNull();
		assertThat(admin.getEmail()).isEqualTo("create@test.com");
		assertThat(admin.getIsDeleted()).isFalse();
	}

	// -------- FIND ALL --------
	@Test
	void testFindAll_excludesDeleted() {
		createAdmin("active@test.com", false);
		createAdmin("deleted@test.com", true);

		List<Admin> admins = adminRepository.findAll();

		assertThat(admins).extracting(Admin::getEmail).contains("active@test.com");
		assertThat(admins).extracting(Admin::getEmail).doesNotContain("deleted@test.com");
	}

	// -------- FIND BY ID --------
	@Test
	void testFindById_existingAndActive() {
		Admin active = createAdmin("findbyid@test.com", false);

		Optional<Admin> found = adminRepository.findById(active.getId());

		assertThat(found).isPresent();
		assertThat(found.get().getEmail()).isEqualTo("findbyid@test.com");
	}

	@Test
	void testFindById_deleted_shouldReturnEmpty() {
		Admin deleted = createAdmin("findbyid-deleted@test.com", true);

		Optional<Admin> found = adminRepository.findById(deleted.getId());

		assertThat(found).isEmpty();
	}

	@Test
	void testFindById_notExisting_shouldReturnEmpty() {
		Optional<Admin> found = adminRepository.findById(99999L);

		assertThat(found).isEmpty();
	}

	// -------- FIND BY EMAIL --------
	@Test
	void testFindByEmail_activeAndDeletedBothReturn() {
		createAdmin("email-active@test.com", false);
		createAdmin("email-deleted@test.com", true);

		Optional<Admin> foundActive = adminRepository.findByEmail("email-active@test.com");
		Optional<Admin> foundDeleted = adminRepository.findByEmail("email-deleted@test.com");

		assertThat(foundActive).isPresent();
		assertThat(foundDeleted).isPresent(); // vì method này KHÔNG filter isDeleted
	}

	@Test
	void testFindByEmail_notExisting_shouldReturnEmpty() {
		Optional<Admin> found = adminRepository.findByEmail("notfound@test.com");

		assertThat(found).isEmpty();
	}

	// -------- FIND ACTIVE BY EMAIL --------
	@Test
	void testFindActiveByEmail_shouldReturnOnlyActive() {
		createAdmin("activeonly@test.com", false);
		createAdmin("deletedonly@test.com", true);

		Optional<Admin> foundActive = adminRepository.findActiveByEmail("activeonly@test.com");
		Optional<Admin> foundDeleted = adminRepository.findActiveByEmail("deletedonly@test.com");

		assertThat(foundActive).isPresent();
		assertThat(foundDeleted).isEmpty();
	}

	@Test
	void testFindActiveByEmail_notExisting_shouldReturnEmpty() {
		Optional<Admin> found = adminRepository.findActiveByEmail("notfoundactive@test.com");

		assertThat(found).isEmpty();
	}
}
