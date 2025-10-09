package com.todaii.english.shared.request.server;

import java.util.Set;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateAdminRequest {
	private String newPassword;

	@NotNull
	@Length(min = 1, max = 191)
	private String displayName;

	private String avatarUrl;

	@NotEmpty
	private Set<@NotBlank String> roleCodes;
}