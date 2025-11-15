package com.todaii.english.shared.request.client;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

	@NotBlank(message = "Reset password token cannot be blank")
	private String resetPasswordToken;

	@NotNull(message = "Password cannot be null")
	@Length(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
	private String password;
}
