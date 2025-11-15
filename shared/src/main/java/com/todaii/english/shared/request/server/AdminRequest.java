package com.todaii.english.shared.request.server;

import java.util.Set;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminRequest {

	@Email(message = "Email format is invalid")
	private String email;

	private String password;

	@NotNull(message = "Display name cannot be null")
	@Length(min = 1, max = 191, message = "Display name must be between 1 and 191 characters")
	private String displayName;

	@NotEmpty(message = "At least one role code is required")
	private Set<String> roleCodes;
}
