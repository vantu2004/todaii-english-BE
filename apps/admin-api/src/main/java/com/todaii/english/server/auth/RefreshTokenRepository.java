package com.todaii.english.server.auth;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.AdminRefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<AdminRefreshToken, Long> {
	@Query("SELECT a FROM AdminRefreshToken a WHERE a.admin.email = ?1")
	public List<AdminRefreshToken> findByAdminEmail(String email);
}
