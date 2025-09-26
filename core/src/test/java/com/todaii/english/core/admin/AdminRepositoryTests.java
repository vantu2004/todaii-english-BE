package com.todaii.english.core.admin;

import com.todaii.english.core.admin.admin.Admin;
import com.todaii.english.core.admin.admin.AdminRepository;
import com.todaii.english.core.admin.admin.AdminRole;
import com.todaii.english.shared.enums.AdminStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
class AdminRepositoryTests {

	@Autowired
	private AdminRepository adminRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	void testCreateAdmin() {
		AdminRole superAdminRole = entityManager.find(AdminRole.class, "SUPER_ADMIN");

		Admin admin = Admin.builder().email("nguyenvana@gmail.com").passwordHash("123456").displayName("nguyen van a")
				.status(AdminStatus.PENDING).enabled(false).build();

		admin.addRole(superAdminRole);
		Admin savedAdmin = this.adminRepository.save(admin);

		assertThat(savedAdmin).isNotNull();
		assertThat(savedAdmin.getEmail()).isEqualTo("nguyenvana@gmail.com");
		assertThat(savedAdmin.getRoles()).containsExactly(superAdminRole);
	}

	@Test
	void testFindByEmail() {
		Optional<Admin> admin = this.adminRepository.findByEmail("nguyenvana@gmail.com");

		assertThat(admin).isPresent();
		assertThat(admin.get().getStatus()).isEqualTo(AdminStatus.PENDING);
	}

	@Test
	void testUpdateAdmin() {
		AdminRole superAdminRole = entityManager.find(AdminRole.class, "SUPER_ADMIN");
		AdminRole contentManagerRole = entityManager.find(AdminRole.class, "CONTENT_MANAGER");

		Admin admin = this.adminRepository.findByEmail("nguyenvana@gmail.com").get();
		assertThat(admin).isNotNull();

		admin.setStatus(AdminStatus.ACTIVE);
		admin.setDisplayName("nguyen van b");
		admin.addRole(contentManagerRole);
		Admin updatedAdmin = this.adminRepository.save(admin);

		assertThat(updatedAdmin.getStatus()).isEqualTo(AdminStatus.ACTIVE);
		assertThat(updatedAdmin.getDisplayName()).isEqualTo("Updated Name");
		assertThat(updatedAdmin.getRoles()).containsExactlyInAnyOrder(superAdminRole, contentManagerRole);
	}

	@Test
	void testDeleteAdmin() {
		Admin admin = this.adminRepository.findByEmail("nguyenvana@gmail.com").get();
		assertThat(admin).isNotNull();

		admin.setIsDeleted(true);
		admin.setDeletedAt(LocalDateTime.now());
		this.adminRepository.save(admin);

		Admin deleted = this.adminRepository.findById(admin.getId()).orElseThrow();
		assertThat(deleted.getIsDeleted()).isTrue();
		assertThat(deleted.getDeletedAt()).isNotNull();
	}
}
