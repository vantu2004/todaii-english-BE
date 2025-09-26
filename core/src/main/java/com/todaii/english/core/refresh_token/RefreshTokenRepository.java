package com.todaii.english.core.refresh_token;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	@Query("SELECT r FROM RefreshToken r WHERE r.admin.email = ?1")
	public List<RefreshToken> findByAdminEmail(String email);

	@Query("SELECT r FROM RefreshToken r WHERE r.user.email = ?1")
	public List<RefreshToken> findByUserEmail(String email);
}
