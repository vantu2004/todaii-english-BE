package com.todaii.english.server.video;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.Video;
import com.todaii.english.shared.dto.VideoDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/video")
public class VideoApiController {
	private final VideoService videoService;

	@GetMapping("/youtube")
	public ResponseEntity<?> importFromYoutube(@RequestParam @NotNull @Length(max = 1024) String url)
			throws BadRequestException {
		return ResponseEntity.ok(videoService.importFromYoutube(url));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getVideo(@PathVariable Long id) {
		return ResponseEntity.ok(videoService.findById(id));
	}

	@GetMapping
	public ResponseEntity<?> getAllVideos() {
		List<Video> videos = videoService.findAll();
		if (videos.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(videos);
	}

	@PostMapping
	public ResponseEntity<?> createVideo(@Valid @RequestBody VideoDTO videoDTO) {
		return ResponseEntity.status(201).body(videoService.createVideo(videoDTO));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateVideo(@PathVariable Long id, @Valid @RequestBody VideoDTO videoDTO) {
		return ResponseEntity.status(200).body(videoService.updateVideo(id, videoDTO));
	}

	@PatchMapping("/{id}/enabled")
	public ResponseEntity<?> toggleEnabled(@PathVariable Long id) {
		videoService.toggleEnabled(id);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteVideo(@PathVariable Long id) {
		videoService.deleteVideo(id);
		return ResponseEntity.noContent().build();
	}
}
