package com.todaii.english.server.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
	@Deprecated
	@Query("SELECT a FROM Admin a WHERE a.isDeleted = false")
	public List<Admin> findAll();

	@Query("""
			SELECT a FROM Admin a
			WHERE a.isDeleted = false
			AND (
			    ?1 IS NULL
			    OR STR(a.id) LIKE CONCAT('%', ?1, '%')
			    OR LOWER(a.email) LIKE LOWER(CONCAT('%', ?1, '%'))
			    OR LOWER(a.displayName) LIKE LOWER(CONCAT('%', ?1, '%'))
			)
			""")
	public Page<Admin> findAllActive(String keyword, Pageable pageable);

	@Query("SELECT a FROM Admin a WHERE a.isDeleted = false AND a.id = ?1")
	public Optional<Admin> findById(Long id);

	public Optional<Admin> findByEmail(String email);

	@Query("SELECT a FROM Admin a WHERE a.email = ?1 AND a.isDeleted = false")
	public Optional<Admin> findActiveByEmail(String email);

}
