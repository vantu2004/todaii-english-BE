package com.todaii.english.shared.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AIResponse {
	private String text;
	private int inputToken;
	private int outputToken;
}
