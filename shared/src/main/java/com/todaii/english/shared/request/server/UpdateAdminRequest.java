package com.todaii.english.shared.request.server;

import java.util.Set;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	@JsonProperty("new_password")
	private String newPassword;

	@NotNull
	@Length(min = 1, max = 191)
	@JsonProperty("display_name")
	private String displayName;

	@JsonProperty("avatar_url")
	private String avatarUrl;

	@NotEmpty
	@JsonProperty("role_codes")
	private Set<@NotBlank String> roleCodes;
}