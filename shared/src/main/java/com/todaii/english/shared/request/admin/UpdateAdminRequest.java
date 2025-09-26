package com.todaii.english.shared.request.admin;

import java.util.Set;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAdminRequest {
	// đổi mật khẩu
	private String oldPassword;
	private String newPassword;

	@NotNull
	@Length(min = 1, max = 191)
	@JsonProperty("display_name")
	private String displayName;

	@JsonProperty("avatar_url")
	private String avatarUrl;

	// chỉ SUPER_ADMIN mới được phép update
	@JsonProperty("role_codes")
	private Set<String> roleCodes;
}
