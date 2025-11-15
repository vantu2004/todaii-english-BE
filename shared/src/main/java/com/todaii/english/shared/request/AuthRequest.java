package com.todaii.english.shared.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {

	@Email(message = "Email format is invalid")
	private String email;

	@NotNull(message = "Password cannot be null")
	@Length(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
	private String password;
}
