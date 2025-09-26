package com.todaii.english.core.admin.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
	@Query("SELECT a FROM Admin a WHERE a.isDeleted = false")
	public List<Admin> findAll();

	@Query("SELECT a FROM Admin a WHERE a.isDeleted = false AND a.id = ?1")
	public Optional<Admin> findById(Long id);

	public Optional<Admin> findByEmail(String email);

}
