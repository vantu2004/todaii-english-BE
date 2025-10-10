package com.todaii.english.server.video;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.VideoLyricLine;

@Repository
public interface VideoLyricLineRepository extends JpaRepository<VideoLyricLine, Long> {
	@Query("SELECT v FROM VideoLyricLine v WHERE v.video.id = ?1 ORDER BY v.lineOrder ASC")
	public List<VideoLyricLine> findAll(Long videoId);

	public Optional<VideoLyricLine> findByIdAndVideoId(Long lineId, Long videoId);

	public boolean existsByIdAndVideoId(Long lineId, Long videoId);

	public void deleteByIdAndVideoId(Long lineId, Long videoId);

	public void deleteAllByVideoId(Long videoId);

}
