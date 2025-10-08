package com.todaii.english.server.video;

import org.apache.coyote.BadRequestException;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping("/fetch")
	public ResponseEntity<?> importFromYoutube(@RequestParam @NotNull @Length(max = 1024) String url)
			throws BadRequestException {
		return ResponseEntity.ok(videoService.importFromYoutube(url));
	}

//	@PostMapping
//	public ResponseEntity<?> createVideo(@Valid @RequestBody VideoDTO videoDTO) {
//		return ResponseEntity.status(201).body(videoService.createVideo(videoDTO));
//	}
}
