package com.todaii.english.server.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.todaii.english.core.entity.Admin;
import com.todaii.english.core.entity.AdminRole;
import com.todaii.english.core.port.CloudinaryPort;
import com.todaii.english.core.security.PasswordHasher;
import com.todaii.english.core.smtp.SmtpService;
import com.todaii.english.shared.enums.AdminStatus;
import com.todaii.english.shared.enums.error_code.AdminErrorCode;
import com.todaii.english.shared.enums.error_code.AuthErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.UpdateProfileRequest;
import com.todaii.english.shared.request.server.AdminRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final AdminRepository adminRepository;
	private final AdminRoleRepository adminRoleRepository;
	private final PasswordHasher passwordHasher;
	private final CloudinaryPort cloudinaryPort;
	private final SmtpService smtpService;

	@Deprecated
	public List<Admin> findAll() {
		// chỉ lấy những admin chưa bị xóa
		return this.adminRepository.findAll();
	}

	public Page<Admin> findAllPaged(Long currentAdminId, int page, int size, String sortBy, String direction,
			String keyword) {
		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
		Pageable pageable = PageRequest.of(page - 1, size, sort);

		return this.adminRepository.findAllActive(currentAdminId, keyword, pageable);
	}

	public Admin findById(Long id) {
		// chỉ lấy những admin chưa bị xóa
		return this.adminRepository.findById(id)
				.orElseThrow(() -> new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));
	}

	public Admin create(AdminRequest request) {
		// chấp nhận lấy cả admin đã bị xóa để check
		if (this.adminRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new BusinessException(AdminErrorCode.ADMIN_ALREADY_EXISTS);
		}

		if (StringUtils.hasText(request.getPassword())) {
			if (request.getPassword().length() < 6 || request.getPassword().length() > 20) {
				throw new BusinessException(AuthErrorCode.PASSWORD_INVALID_LENGTH);
			}
		} else {
			throw new BusinessException(400, "Password not blank");
		}

		String passwordHash = this.passwordHasher.hash(request.getPassword());

		// tìm role trong db
		Set<AdminRole> roles = this.getAdminRoles(request.getRoleCodes());

		Admin admin = Admin.builder().email(request.getEmail()).passwordHash(passwordHash)
				.displayName(request.getDisplayName()).status(AdminStatus.PENDING).roles(roles).build();

		return this.adminRepository.save(admin);
	}

	public Admin updateProfile(Long id, UpdateProfileRequest request) {
		Admin admin = this.adminRepository.findById(id)
				.orElseThrow(() -> new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

		if (StringUtils.hasText(request.getNewPassword())) {
			if (request.getNewPassword().length() < 6 || request.getNewPassword().length() > 20) {
				throw new BusinessException(AuthErrorCode.PASSWORD_INVALID_LENGTH);
			}

			if (!StringUtils.hasText(request.getOldPassword())
					|| !passwordHasher.matches(request.getOldPassword(), admin.getPasswordHash())) {
				throw new BusinessException(AuthErrorCode.PASSWORD_INCORRECT);
			}

			admin.setPasswordHash(passwordHasher.hash(request.getNewPassword()));
		}

		String avatar = request.getAvatarUrl();
		if (StringUtils.hasText(avatar) && request.getAvatarUrl().startsWith("data:image")) {
			String uploadedUrl = cloudinaryPort.uploadImage(avatar, "admin_avatars");
			admin.setAvatarUrl(uploadedUrl);

		} else {
			admin.setAvatarUrl(admin.getAvatarUrl());
		}

		admin.setDisplayName(request.getDisplayName());

		return this.adminRepository.save(admin);
	}

	public Admin updateAdmin(Long id, AdminRequest request) {
		Admin admin = this.adminRepository.findById(id)
				.orElseThrow(() -> new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

		// Xử lý password (super admin không cần oldPassword)
		if (StringUtils.hasText(request.getPassword())) {
			if (request.getPassword().length() < 6 || request.getPassword().length() > 20) {
				throw new BusinessException(AuthErrorCode.PASSWORD_INVALID_LENGTH);
			}
			admin.setPasswordHash(passwordHasher.hash(request.getPassword()));
		}

		admin.setDisplayName(request.getDisplayName());

		// Update roles
		if (request.getRoleCodes() != null && !request.getRoleCodes().isEmpty()) {
			Set<AdminRole> roles = this.getAdminRoles(request.getRoleCodes());
			admin.setRoles(roles);
		}

		return this.adminRepository.save(admin);
	}

	private Set<AdminRole> getAdminRoles(Set<String> roleCodes) {
		Set<AdminRole> roles = roleCodes.stream()
				.map(code -> this.adminRoleRepository.findById(code)
						.orElseThrow(() -> new BusinessException(AdminErrorCode.ROLE_NOT_FOUND)))
				.collect(Collectors.toSet());
		return roles;
	}

	public void delete(Long id) {
		Admin admin = this.adminRepository.findById(id)
				.orElseThrow(() -> new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

		smtpService.accountDeletedNotice(admin.getEmail(), admin.getDisplayName());

		admin.setIsDeleted(true);
		admin.setDeletedAt(LocalDateTime.now());
		admin.setEnabled(false);
		admin.setStatus(AdminStatus.LOCKED);

		this.adminRepository.save(admin);
	}

	public void updateLastLogin(String email) {
		Admin admin = this.adminRepository.findActiveByEmail(email)
				.orElseThrow(() -> new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

		admin.setLastLoginAt(LocalDateTime.now());

		this.adminRepository.save(admin);
	}

	public void toggleEnabled(Long id) {
		Admin admin = this.adminRepository.findById(id)
				.orElseThrow(() -> new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

		// Đảo ngược trạng thái enable
		admin.setEnabled(!admin.getEnabled());

		// Nếu disable thì đổi status về LOCKED, nếu enable thì ACTIVE
		if (admin.getEnabled()) {
			admin.setStatus(AdminStatus.ACTIVE);

			smtpService.accountUnBannedNotice(admin.getEmail(), admin.getDisplayName());
		} else {
			admin.setStatus(AdminStatus.LOCKED);

			smtpService.accountBannedNotice(admin.getEmail(), admin.getDisplayName());
		}

		this.adminRepository.save(admin);
	}

}
