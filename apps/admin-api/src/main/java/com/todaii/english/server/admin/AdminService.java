package com.todaii.english.server.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.todaii.english.core.entity.Admin;
import com.todaii.english.core.entity.AdminRole;
import com.todaii.english.core.security.PasswordHasher;
import com.todaii.english.core.smtp.SmtpService;
import com.todaii.english.shared.enums.AdminStatus;
import com.todaii.english.shared.enums.error_code.AdminErrorCode;
import com.todaii.english.shared.enums.error_code.AuthErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.UpdateProfileRequest;
import com.todaii.english.shared.request.VerifyOtpRequest;
import com.todaii.english.shared.request.server.CreateAdminRequest;
import com.todaii.english.shared.request.server.UpdateAdminRequest;
import com.todaii.english.shared.utils.OtpUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final AdminRepository adminRepository;
	private final AdminRoleRepository adminRoleRepository;
	private final PasswordHasher passwordHasher;
	private final SmtpService smtpService;

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

		String otp = OtpUtils.generateOtp();
		this.smtpService.sendVerifyEmail(request.getEmail(), otp);

		Admin admin = Admin.builder().email(request.getEmail()).passwordHash(passwordHash)
				.displayName(request.getDisplayName()).otp(otp).otpExpiredAt(LocalDateTime.now().plusMinutes(15))
				.status(AdminStatus.PENDING).roles(roles).build();

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

		admin.setDisplayName(request.getDisplayName());
		admin.setAvatarUrl(request.getAvatarUrl());

		return this.adminRepository.save(admin);
	}

	public Admin updateAdmin(Long id, UpdateAdminRequest request) {
		Admin admin = this.adminRepository.findById(id)
				.orElseThrow(() -> new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

		// 1. Xử lý password (super admin không cần oldPassword)
		if (StringUtils.hasText(request.getNewPassword())) {
			if (request.getNewPassword().length() < 6 || request.getNewPassword().length() > 20) {
				throw new BusinessException(AuthErrorCode.PASSWORD_INVALID_LENGTH);
			}
			admin.setPasswordHash(passwordHasher.hash(request.getNewPassword()));
		}

		// 2. Update displayName + avatar
		admin.setDisplayName(request.getDisplayName());
		admin.setAvatarUrl(request.getAvatarUrl());

		// 3. Update roles
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

		admin.setIsDeleted(true);
		admin.setDeletedAt(LocalDateTime.now());
		admin.setEnabled(false);
		admin.setStatus(AdminStatus.LOCKED);

		this.adminRepository.save(admin);
	}

	public void verifyOtp(VerifyOtpRequest verifyOtpRequest) {
		Admin admin = this.adminRepository.findActiveByEmail(verifyOtpRequest.getEmail())
				.orElseThrow(() -> new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

		if (admin.getOtp() == null || admin.getOtpExpiredAt() == null) {
			throw new BusinessException(AuthErrorCode.OTP_NOT_FOUND);
		}
		if (admin.getOtpExpiredAt().isBefore(LocalDateTime.now())) {
			throw new BusinessException(AuthErrorCode.OTP_EXPIRED);
		}
		if (!verifyOtpRequest.getOtp().equals(admin.getOtp())) {
			throw new BusinessException(AuthErrorCode.OTP_INVALID);
		}

		// Nếu hợp lệ thì clear OTP và active account
		admin.setOtp(null);
		admin.setOtpExpiredAt(null);
		admin.setEnabled(true);
		admin.setStatus(AdminStatus.ACTIVE);

		this.adminRepository.save(admin);
	}

	public void resendOtp(String email) {
		Admin admin = this.adminRepository.findActiveByEmail(email)
				.orElseThrow(() -> new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

		if (admin.getEnabled() || !admin.getStatus().equals(AdminStatus.PENDING)) {
			throw new BusinessException(AuthErrorCode.ALREADY_VERIFIED);
		}

		String otp = OtpUtils.generateOtp();

		admin.setOtp(otp);
		admin.setOtpExpiredAt(LocalDateTime.now().plusMinutes(15));
		this.adminRepository.save(admin);

		this.smtpService.sendVerifyEmail(email, otp);
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
			admin.setOtp(null);
			admin.setOtpExpiredAt(null);
		} else {
			admin.setStatus(AdminStatus.LOCKED);
		}

		this.adminRepository.save(admin);
	}
}
