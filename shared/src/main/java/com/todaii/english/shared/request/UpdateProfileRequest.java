package com.todaii.english.shared.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateProfileRequest {

	private String oldPassword;

	private String newPassword;

	@NotNull(message = "Display name cannot be null")
	@Length(min = 1, max = 191, message = "Display name must be between 1 and 191 characters")
	private String displayName;

	private String avatarUrl;
}
