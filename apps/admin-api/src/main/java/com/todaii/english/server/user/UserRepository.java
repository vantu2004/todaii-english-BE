package com.todaii.english.server.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	@Deprecated
	@Query("SELECT u FROM User u WHERE u.isDeleted = false")
	public List<User> findAll();

	@Query("""
			SELECT u FROM User u
			WHERE u.isDeleted = false
			AND (
			    ?1 IS NULL
			    OR STR(u.id) LIKE CONCAT('%', ?1, '%')
			    OR LOWER(u.email) LIKE LOWER(CONCAT('%', ?1, '%'))
			    OR LOWER(u.displayName) LIKE LOWER(CONCAT('%', ?1, '%'))
			)
			""")
	public Page<User> findAllActive(String keyword, Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.id = ?1 AND u.isDeleted = false")
	public Optional<User> findById(Long id);
}
