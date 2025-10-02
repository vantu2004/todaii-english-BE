package com.todaii.english.core.server.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
	@Query("SELECT a FROM Admin a WHERE a.isDeleted = false")
	public List<Admin> findAll();

	@Query("SELECT a FROM Admin a WHERE a.isDeleted = false AND a.id = ?1")
	public Optional<Admin> findById(Long id);

	public Optional<Admin> findByEmail(String email);

	@Query("SELECT a FROM Admin a WHERE a.email = ?1 AND a.isDeleted = false")
	public Optional<Admin> findActiveByEmail(String email);

}
