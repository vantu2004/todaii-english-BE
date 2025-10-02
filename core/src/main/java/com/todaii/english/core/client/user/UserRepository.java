package com.todaii.english.core.client.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	public Optional<User> findByEmail(String email);

	@Query("SELECT u FROM User u WHERE u.email = ?1 AND u.isDeleted = false")
	public Optional<User> findActiveByEmail(String email);

	@Query("SELECT u FROM User u WHERE u.resetPasswordToken = ?1 AND u.isDeleted = false")
	public Optional<User> findByResetPasswordToken(String token);

	@Query("SELECT u FROM User u WHERE u.id = ?1 AND u.isDeleted = false")
	public Optional<User> findById(Long id);
}
