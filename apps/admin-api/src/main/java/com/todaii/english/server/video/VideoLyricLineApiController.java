package com.todaii.english.server.video;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.todaii.english.core.entity.VideoLyricLine;
import com.todaii.english.shared.dto.VideoLyricLineDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lyric")
public class VideoLyricLineApiController {
	private final VideoLyricLineService videoLyricLineService;

	@PostMapping("/import")
	public ResponseEntity<?> importLyrics(@RequestParam("file") MultipartFile file) {
		return ResponseEntity.ok(videoLyricLineService.importFromSrt(file));
	}

	@GetMapping("/video/{videoId}")
	public ResponseEntity<?> getAllLyrics(@PathVariable Long videoId) {
		List<VideoLyricLine> videoLyricLines = videoLyricLineService.findAll(videoId);
		if (videoLyricLines.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(videoLyricLines);
	}

	@GetMapping("video/{videoId}/line/{lineId}")
	public ResponseEntity<?> getLyric(@PathVariable Long videoId, @PathVariable Long lineId) {
		return ResponseEntity.ok(videoLyricLineService.findByVideoIdAndLineId(videoId, lineId));
	}

	@PostMapping("/video/{videoId}/batch")
	public ResponseEntity<?> createMultipleLyrics(@PathVariable Long videoId,
			@Valid @RequestBody List<VideoLyricLineDTO> videoLyricLineDTOs) {
		return ResponseEntity.status(201).body(videoLyricLineService.createBatch(videoId, videoLyricLineDTOs));
	}

	@PutMapping("/video/{videoId}/line/{lineId}")
	public ResponseEntity<?> updateLyric(@PathVariable Long videoId, @PathVariable Long lineId,
			@Valid @RequestBody VideoLyricLineDTO videoLyricLineDTOs) {
		return ResponseEntity.ok(videoLyricLineService.updateLyric(videoId, lineId, videoLyricLineDTOs));
	}

	@DeleteMapping("/video/{videoId}/line/{lineId}")
	public ResponseEntity<?> deleteLyric(@PathVariable Long videoId, @PathVariable Long lineId) {
		videoLyricLineService.deleteLyric(videoId, lineId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/video/{videoId}")
	public ResponseEntity<?> deleteAllLyrics(@PathVariable Long videoId) {
		videoLyricLineService.deleteAllLyrics(videoId);
		return ResponseEntity.noContent().build();
	}
}
