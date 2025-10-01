package com.todaii.english.core.refresh_token;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {
	@Query("SELECT u FROM UserRefreshToken u WHERE u.user.email = ?1")
	public List<UserRefreshToken> findByUserEmail(String email);
}
