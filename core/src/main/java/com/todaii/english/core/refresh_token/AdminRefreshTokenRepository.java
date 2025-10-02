package com.todaii.english.core.refresh_token;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.todaii.english.core.entity.AdminRefreshToken;

public interface AdminRefreshTokenRepository extends JpaRepository<AdminRefreshToken, Long> {
	@Query("SELECT a FROM AdminRefreshToken a WHERE a.admin.email = ?1")
	public List<AdminRefreshToken> findByAdminEmail(String email);
}
