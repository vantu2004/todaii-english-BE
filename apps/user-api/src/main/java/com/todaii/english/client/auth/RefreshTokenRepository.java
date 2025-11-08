package com.todaii.english.client.auth;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.todaii.english.core.entity.UserRefreshToken;

public interface RefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {
	@Query("SELECT u FROM UserRefreshToken u WHERE u.user.email = ?1")
	public List<UserRefreshToken> findByUserEmail(String email);
}
