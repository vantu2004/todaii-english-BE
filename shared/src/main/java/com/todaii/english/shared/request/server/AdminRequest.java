package com.todaii.english.shared.request.server;

import java.util.Set;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminRequest {
	@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email format is invalid")
	private String email;

	private String password;

	@NotNull
	@Length(min = 1, max = 191)
	private String displayName;

	@NotEmpty
	private Set<String> roleCodes;
}
