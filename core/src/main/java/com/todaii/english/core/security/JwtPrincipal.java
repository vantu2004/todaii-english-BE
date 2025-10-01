package com.todaii.english.core.security;

import java.util.Set;

// phục vụ validate chung
public interface JwtPrincipal {
	Long getId();

	String getDisplayName();

	default Set<String> getRoleCodes() {
		return Set.of();
	} // mặc định rỗng (tính choi trường hợp của USER)

	String getActorType(); // "ADMIN" hoặc "USER"
}
