package com.todaii.english.client.video;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.VideoLyricLine;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/video")
public class VideoLyricLineApiController {
	private final VideoLyricLineService videoLyricLineService;

	@GetMapping("/{videoId}/lyric")
	public ResponseEntity<List<VideoLyricLine>> getAllLyricsByVideoId(@PathVariable Long videoId) {
		return ResponseEntity.ok(videoLyricLineService.findAllByVideoId(videoId));
	}
}
