package com.todaii.english.server.vocabulary;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.VocabGroup;

@Repository
public interface VocabGroupRepository extends JpaRepository<VocabGroup, Long> {
	@Query("SELECT v FROM VocabGroup v WHERE v.id = ?1 AND v.isDeleted = false")
	public Optional<VocabGroup> findById(Long id);

	@Deprecated
	@Query("SELECT v FROM VocabGroup v WHERE v.isDeleted = false")
	public List<VocabGroup> findAll();

	@Query("""
			SELECT v FROM VocabGroup v
			WHERE v.isDeleted = false
			AND (
			    ?1 IS NULL
			    OR STR(v.id) LIKE CONCAT('%', ?1, '%')
			    OR LOWER(v.name) LIKE LOWER(CONCAT('%', ?1, '%'))
			    OR LOWER(v.alias) LIKE LOWER(CONCAT('%', ?1, '%'))
			)
			""")
	public Page<VocabGroup> search(String keyword, Pageable pageable);

	public boolean existsByAlias(String alias);
}
