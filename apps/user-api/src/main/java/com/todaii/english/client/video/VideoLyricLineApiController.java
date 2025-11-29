package com.todaii.english.client.video;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.VideoLyricLine;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/video")
@Tag(name = "Video Lyrics", description = "APIs for video lyrics and lyric lines")
public class VideoLyricLineApiController {
	private final VideoLyricLineService videoLyricLineService;

	@GetMapping("/{videoId}/lyric")
	@Operation(summary = "Get all lyrics for a video", description = "Retrieve all lyric lines for a specific video by its ID")
	public ResponseEntity<List<VideoLyricLine>> getAllLyricsByVideoId(@PathVariable Long videoId) {
		return ResponseEntity.ok(videoLyricLineService.findAllByVideoId(videoId));
	}
}
