package com.todaii.english.server.vocabulary;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.VocabGroup;

@Repository
public interface VocabGroupRepository extends JpaRepository<VocabGroup, Long> {
	@Query("SELECT v FROM VocabGroup v WHERE v.id = ?1 AND v.isDeleted = false")
	public Optional<VocabGroup> findById(Long id);

	@Query("SELECT v FROM VocabGroup v WHERE v.isDeleted = false")
	public List<VocabGroup> findAll();

	public boolean existsByAlias(String alias);
}
