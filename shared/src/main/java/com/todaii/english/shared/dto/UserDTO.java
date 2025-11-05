package com.todaii.english.shared.dto;

import java.time.LocalDateTime;

import com.todaii.english.shared.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserDTO {
	private Long id;
	private String email;
	private String displayName;
	private String avatarUrl;
	private Boolean enabled;
	private UserStatus status;
	private LocalDateTime emailVerifiedAt;
	private LocalDateTime lastLoginAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
