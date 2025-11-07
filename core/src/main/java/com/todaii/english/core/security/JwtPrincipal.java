package com.todaii.english.core.security;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

// phục vụ validate chung
public interface JwtPrincipal {
	Long getId();

	String getDisplayName();

	@JsonIgnore
	default Set<String> getRoleCodes() {
		return Set.of();
	} // mặc định rỗng (tính cho trường hợp của USER)

	String getActorType(); // "ADMIN" hoặc "USER"
}
