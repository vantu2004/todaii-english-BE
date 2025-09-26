package com.todaii.english.core.admin.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.todaii.english.core.security.PasswordHasher;
import com.todaii.english.shared.enums.AdminStatus;
import com.todaii.english.shared.enums.error_code.AdminErrorCode;
import com.todaii.english.shared.enums.error_code.AuthErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.admin.CreateAdminRequest;
import com.todaii.english.shared.request.admin.UpdateAdminRequest;
import com.todaii.english.shared.utils.OtpUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final AdminRepository adminRepository;
	private final AdminRoleRepository adminRoleRepository;
	private final PasswordHasher passwordHasher;

	public List<Admin> findAll() {
		// chỉ lấy những admin chưa bị xóa
		return this.adminRepository.findAll();
	}

	public Admin findById(Long id) {
		// chỉ lấy những admin chưa bị xóa
		return this.adminRepository.findById(id)
				.orElseThrow(() -> new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));
	}

	public Admin create(CreateAdminRequest request) {
		// chấp nhận lấy cả admin đã bị xóa để check
		if (this.adminRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new BusinessException(AdminErrorCode.ADMIN_ALREADY_EXISTS);
		}

		String passwordHash = this.passwordHasher.hash(request.getPassword());

		// tìm role trong db
		Set<AdminRole> roles = this.getAdminRoles(request.getRoleCodes());

		String OTP = OtpUtils.generateOtp();

		Admin admin = Admin.builder().email(request.getEmail()).passwordHash(passwordHash)
				.displayName(request.getDisplayName()).OTP(OTP).status(AdminStatus.PENDING).roles(roles).build();

		return adminRepository.save(admin);
	}

	public Admin update(Long id, UpdateAdminRequest request) {
		Admin admin = this.adminRepository.findById(id)
				.orElseThrow(() -> new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

		// check role
		boolean isSuperAdmin = admin.getRoles().stream().anyMatch(r -> "SUPER_ADMIN".equals(r.getCode()));

		if (isSuperAdmin) {
			if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
				if (request.getNewPassword().length() < 6 || request.getNewPassword().length() > 20) {
					throw new BusinessException(AuthErrorCode.PASSWORD_INVALID_LENGTH);
				}

				admin.setPasswordHash(passwordHasher.hash(request.getNewPassword()));
			}

			// SUPER_ADMIN có quyền chỉnh role
			admin.setRoles(getAdminRoles(request.getRoleCodes()));
		}

		// 1. Xử lý password
		if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
			if (request.getNewPassword().length() < 6 || request.getNewPassword().length() > 20) {
				throw new BusinessException(AuthErrorCode.PASSWORD_INVALID_LENGTH);
			}

			// nếu có oldPassword → verify
			if (!passwordHasher.matches(request.getOldPassword(), admin.getPasswordHash())) {
				throw new BusinessException(AuthErrorCode.PASSWORD_INCORRECT);
			}

			admin.setPasswordHash(passwordHasher.hash(request.getNewPassword()));
		}

		// 2. Xử lý role
		if (isSuperAdmin) {

		} else {
			throw new BusinessException(AdminErrorCode.ADMIN_FORBIDDEN);
		}

		// 3. Update thông tin cơ bản
		admin.setDisplayName(request.getDisplayName());
		admin.setAvatarUrl(request.getAvatarUrl());

		return adminRepository.save(admin);
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

		admin.setIsDeleted(true);
		admin.setDeletedAt(LocalDateTime.now());

		this.adminRepository.save(admin);
	}

}
