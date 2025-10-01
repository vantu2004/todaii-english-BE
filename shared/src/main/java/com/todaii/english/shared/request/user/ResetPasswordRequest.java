package com.todaii.english.shared.request.user;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
	@NotBlank
	private String resetPasswordToken;

	@NotNull
	@Length(min = 6, max = 20)
	private String password;
}
