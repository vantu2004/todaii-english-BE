package com.todaii.english.shared.request.server;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettingRequest {

	@NotEmpty(message = "Settings map cannot be empty")
	private Map<@NotBlank(message = "Setting key cannot be blank") String, @NotBlank(message = "Setting value cannot be blank") String> settings;
}
