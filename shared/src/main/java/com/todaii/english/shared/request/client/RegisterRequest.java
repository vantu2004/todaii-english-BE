package com.todaii.english.shared.request.client;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

	@Email(message = "Email format is invalid")
	private String email;

	@NotNull(message = "Password cannot be null")
	@Length(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
	private String password;

	@NotNull(message = "Display name cannot be null")
	@Length(min = 1, max = 191, message = "Display name must be between 1 and 191 characters")
	private String displayName;
}
