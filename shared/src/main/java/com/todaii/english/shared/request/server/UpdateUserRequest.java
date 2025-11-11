package com.todaii.english.shared.request.server;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
	private String newPassword;

	@NotNull
	@Length(min = 1, max = 191)
	private String displayName;

}
