package com.todaii.english.client.video;

import java.util.List;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.VideoLyricLine;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoLyricLineService {
	private final VideoLyricLineRepository videoLyricLineRepository;

	public List<VideoLyricLine> findAllByVideoId(Long videoId) {
		return videoLyricLineRepository.findAllByVideoId(videoId);
	}
}
