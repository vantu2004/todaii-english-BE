package com.todaii.english.shared.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
	private String accessToken;
	private String refreshToken;
}
