package com.todaii.english.client.video;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.VideoLyricLine;

@Repository
public interface VideoLyricLineRepository extends JpaRepository<VideoLyricLine, Long> {
	public List<VideoLyricLine> findAllByVideoId(Long videoId);

}
