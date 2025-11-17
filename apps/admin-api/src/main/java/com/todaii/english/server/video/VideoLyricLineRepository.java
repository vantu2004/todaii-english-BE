package com.todaii.english.server.video;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.VideoLyricLine;

@Repository
public interface VideoLyricLineRepository extends JpaRepository<VideoLyricLine, Long> {
	@Query("SELECT v FROM VideoLyricLine v WHERE v.video.id = ?1 ORDER BY v.lineOrder ASC")
	public List<VideoLyricLine> findAll(Long videoId);

	public void deleteAllByVideoId(Long videoId);

	public boolean existsByVideoId(Long id);

	@Query("""
			SELECT v FROM VideoLyricLine v
			WHERE
			    ( v.video.id = ?1) AND
			    (
			        ?2 IS NULL
			        OR STR(v.id) LIKE CONCAT('%', ?2, '%')
			        OR STR(v.lineOrder) LIKE CONCAT('%', ?2, '%')
			        OR STR(v.startMs) LIKE CONCAT('%', ?2, '%')
			        OR STR(v.endMs) LIKE CONCAT('%', ?2, '%')
			        OR v.textEn LIKE CONCAT('%', ?2, '%')
			        OR v.textVi LIKE CONCAT('%', ?2, '%')
			    )
			""")
	public List<VideoLyricLine> findAll(Long videoId, String keyword, Sort sort);

}
