package com.todaii.english.client.video;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.core.entity.Video;
import com.todaii.english.shared.enums.CefrLevel;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long>, JpaSpecificationExecutor<Video> {
	public Page<Video> findAllByEnabledTrueOrderByCreatedAtDesc(Pageable pageable);

	public Page<Video> findAllByEnabledTrueOrderByViewsDesc(Pageable pageable);

	@Query("""
			    SELECT DISTINCT v
			    FROM Video v
			    JOIN v.topics t
			    WHERE v.enabled = true
			      AND v.id <> ?1
			      AND t.id IN ?2
			""")
	public List<Video> findRelatedByTopics(Long videoId, List<Long> topicIds);

	@Query("""
			    SELECT v
			    FROM Video v
			    WHERE v.enabled = true
			      AND v.id <> ?1
			      AND v.cefrLevel = ?2
			""")
	public List<Video> findFallbackByCefr(Long videoId, CefrLevel level, Pageable pageable);

	@Query("""
			    SELECT v FROM Video v
			    WHERE v.enabled = true
			      AND v.createdAt BETWEEN ?1 AND ?2
			""")
	public Page<Video> findByCreatedDateRange(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);

	@Query("""
			    SELECT d FROM Video v
			    JOIN v.words d
			    WHERE v.id = ?1
			    ORDER BY d.headword ASC
			""")
	public Page<DictionaryEntry> findPagedWordsByVideoId(Long id, Pageable pageable);
}
