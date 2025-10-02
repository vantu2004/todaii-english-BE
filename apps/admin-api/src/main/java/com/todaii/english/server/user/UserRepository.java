package com.todaii.english.server.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	@Query("SELECT u FROM User u WHERE u.isDeleted = false")
	public List<User> findAll();

	@Query("SELECT u FROM User u WHERE u.id = ?1 AND u.isDeleted = false")
	public Optional<User> findById(Long id);
}
