package com.todaii.english.shared.request.client;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
	@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email format is invalid")
	private String email;

	@NotNull
	@Length(min = 6, max = 20)
	private String password;

	@NotNull
	@Length(min = 1, max = 191)
	@JsonProperty("display_name")
	private String displayName;
}