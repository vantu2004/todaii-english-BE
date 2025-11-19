package com.todaii.english.server.video;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
	public boolean existsByVideoUrl(String videoUrl);

	// nếu topicId != null nghĩa là tìm các video dựa theo topicId, dùng chung cho 2 hàm getVideos
	@Query("""
			SELECT DISTINCT v FROM Video v
			LEFT JOIN v.topics t
			WHERE
			    (?1 IS NULL OR t.id = ?1) AND
			    (
			        ?2 IS NULL
			        OR STR(v.id) LIKE CONCAT('%', ?2, '%')
			        OR LOWER(v.title) LIKE LOWER(CONCAT('%', ?2, '%'))
			        OR LOWER(v.authorName) LIKE LOWER(CONCAT('%', ?2, '%'))
			        OR LOWER(v.providerName) LIKE LOWER(CONCAT('%', ?2, '%'))
			        OR LOWER(v.cefrLevel) LIKE LOWER(CONCAT('%', ?2, '%'))
			    )
			""")
	public Page<Video> search(Long topicId, String keyword, Pageable pageable);

	public List<Video> findAllByWords_Id(Long entryId);
}
