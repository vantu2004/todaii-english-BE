package com.todaii.english.core.entity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.todaii.english.core.security.JwtPrincipal;
import com.todaii.english.shared.enums.AdminStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Admin implements JwtPrincipal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 191, unique = true)
	private String email;

	@Column(name = "password_hash", nullable = false, length = 191)
	private String passwordHash;

	@Column(name = "display_name", length = 191)
	private String displayName;

	@Column(name = "avatar_url", length = 512)
	private String avatarUrl;

	@Column(name = "otp", length = 16)
	private String otp;

	@Column(name = "otp_expired_at")
	private LocalDateTime otpExpiredAt;

	@Builder.Default
	private boolean enabled = false;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private AdminStatus status;

	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;

	@Builder.Default
	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = false;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	// chỉ cần từ phía admin là đủ
	@ManyToMany
	@JoinTable(name = "admins_roles", joinColumns = @JoinColumn(name = "admin_id"), inverseJoinColumns = @JoinColumn(name = "role_code"))
	@Builder.Default
	@NotEmpty(message = "Role cannot be empty")
	private Set<AdminRole> roles = new HashSet<>();

	public void addRole(AdminRole adminRole) {
		this.roles.add(adminRole);
	}

	// chuyển roles về tập string để dùng tạo jwt token
	@Override
	public Set<String> getRoleCodes() {
		return roles.stream().map(AdminRole::getCode).collect(Collectors.toSet());
	}

	@Override
	public String getActorType() {
		return "ADMIN";
	}

}
