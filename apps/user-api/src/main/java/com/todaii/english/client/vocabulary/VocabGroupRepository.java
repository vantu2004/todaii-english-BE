package com.todaii.english.client.vocabulary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.VocabGroup;

@Repository
public interface VocabGroupRepository extends JpaRepository<VocabGroup, Long> {
	@Query("SELECT v FROM VocabGroup v WHERE v.enabled = true AND v.isDeleted = false ORDER BY v.name ASC")
	public List<VocabGroup> findAll();
}
