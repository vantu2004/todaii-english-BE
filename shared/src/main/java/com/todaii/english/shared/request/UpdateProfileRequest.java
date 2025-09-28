package com.todaii.english.shared.request;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {
	@JsonProperty("old_password")
	private String oldPassword;
	
	@JsonProperty("new_password")
	private String newPassword;

	@NotNull
	@Length(min = 1, max = 191)
	@JsonProperty("display_name")
	private String displayName;

	@JsonProperty("avatar_url")
	private String avatarUrl;
}
