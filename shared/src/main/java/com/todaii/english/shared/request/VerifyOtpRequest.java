package com.todaii.english.shared.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpRequest {

	@Email(message = "Email format is invalid")
	private String email;

	@NotBlank(message = "OTP cannot be blank")
	private String otp;
}
