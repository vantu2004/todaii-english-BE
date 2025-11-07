package com.todaii.english.server.video;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
	public boolean existsByVideoUrl(String videoUrl);

	@Query("""
			SELECT DISTINCT v FROM Video v
			WHERE
			    ?1 IS NULL
			    OR STR(v.id) LIKE CONCAT('%', ?1, '%')
			    OR LOWER(v.title) LIKE LOWER(CONCAT('%', ?1, '%'))
			    OR LOWER(v.authorName) LIKE LOWER(CONCAT('%', ?1, '%'))
			    OR LOWER(v.providerName) LIKE LOWER(CONCAT('%', ?1, '%'))
			    OR LOWER(v.cefrLevel) LIKE LOWER(CONCAT('%', ?1, '%'))
			""")
	public Page<Video> search(String keyword, Pageable pageable);
}
