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
@Rollback(true)
class AdminRepositoryTests {

	@Autowired
	private AdminRepository adminRepository;

	@PersistenceContext
	private EntityManager entityManager;

	private AdminRole superAdminRole;

	@BeforeEach
	void setup() {
		// Giả định role này đã có trong DB test
		superAdminRole = AdminRole.builder().code("SUPER_ADMIN").description("Super admin").build();
	}

	private Admin createAdmin(String email, boolean isDeleted) {
		Admin admin = Admin.builder().email(email).passwordHash("pass").displayName("test user")
				.status(AdminStatus.PENDING).enabled(true).isDeleted(isDeleted).build();
		admin.addRole(superAdminRole);
		if (isDeleted) {
			admin.setDeletedAt(LocalDateTime.now());
		}
		return adminRepository.saveAndFlush(admin); // Dùng saveAndFlush để đảm bảo data được ghi vào DB ngay
	}

	private void assertAdminFound(Optional<Admin> optional, String expectedEmail) {
		assertThat(optional).isPresent();
		assertThat(optional.get().getEmail()).isEqualTo(expectedEmail);
		assertThat(optional.get().getIsDeleted()).isFalse(); // Chỉ tìm active
	}

	private void assertAdminNotFound(Optional<Admin> optional) {
		assertThat(optional).isEmpty();
	}

	// -------- CREATE --------
	@Test
	void testCreateAdmin_success() {
		Admin admin = createAdmin("create@test.com", false);

		assertThat(admin.getId()).isNotNull();
		assertThat(admin.getEmail()).isEqualTo("create@test.com");
		assertThat(admin.getIsDeleted()).isFalse();
		// Kiểm tra thêm: Role đã được add thành công
		assertThat(admin.getRoles()).hasSize(1).extracting("code").contains("SUPER_ADMIN");
	}

	// -------- FIND ALL (findAllActive) --------
	// Đổi tên test để phản ánh đúng method của Repository
	@Test
	void testFindAllActive_excludesDeleted() {
		createAdmin("active@test.com", false);
		createAdmin("deleted@test.com", true);

		// Dùng method mới tương ứng với AdminApiController (findAllActive)
		List<Admin> admins = adminRepository.findAllActive(null, null).getContent();

		assertThat(admins).extracting(Admin::getEmail).contains("active@test.com");
		assertThat(admins).extracting(Admin::getEmail).doesNotContain("deleted@test.com");
	}

	// -------- FIND BY ID (findById) --------
	@Test
	void testFindById_existingAndActive_shouldReturnAdmin() {
		Admin active = createAdmin("findbyid-active@test.com", false);

		Optional<Admin> found = adminRepository.findById(active.getId());

		assertAdminFound(found, "findbyid-active@test.com");
	}

	@Test
	void testFindById_deleted_shouldReturnEmpty() {
		Admin deleted = createAdmin("findbyid-deleted@test.com", true);

		Optional<Admin> found = adminRepository.findById(deleted.getId());

		assertAdminNotFound(found);
	}

	// -------- FIND BY EMAIL (findByEmail) --------
	@Test
	void testFindByEmail_activeAndDeletedBothReturn() {
		createAdmin("email-active@test.com", false);
		createAdmin("email-deleted@test.com", true);

		Optional<Admin> foundActive = adminRepository.findByEmail("email-active@test.com");
		Optional<Admin> foundDeleted = adminRepository.findByEmail("email-deleted@test.com");

		assertThat(foundActive).isPresent();
		assertThat(foundActive.get().getIsDeleted()).isFalse();

		assertThat(foundDeleted).isPresent();
		assertThat(foundDeleted.get().getIsDeleted()).isTrue();
	}

	// -------- FIND ACTIVE BY EMAIL (findActiveByEmail) --------
	@Test
	void testFindActiveByEmail_shouldReturnOnlyActive() {
		createAdmin("activeonly@test.com", false);
		createAdmin("deletedonly@test.com", true);

		Optional<Admin> foundActive = adminRepository.findActiveByEmail("activeonly@test.com");
		Optional<Admin> foundDeleted = adminRepository.findActiveByEmail("deletedonly@test.com");

		assertAdminFound(foundActive, "activeonly@test.com");
		assertAdminNotFound(foundDeleted);
	}
}