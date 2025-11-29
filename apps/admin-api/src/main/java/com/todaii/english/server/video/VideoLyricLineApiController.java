package com.todaii.english.server.video;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.todaii.english.core.entity.VideoLyricLine;
import com.todaii.english.shared.dto.VideoLyricLineDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/video")
@Tag(name = "Video Lyrics", description = "APIs for managing video lyrics")
public class VideoLyricLineApiController {

	private final VideoLyricLineService videoLyricLineService;

	@PostMapping("/lyric/import")
	@Operation(
		summary = "Import lyrics from SRT file",
		description = "Upload an SRT file to import multiple video lyric lines",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(type = "string", format = "binary")
			)
		)
	)
	@ApiResponse(responseCode = "200", description = "Lyrics imported successfully")
	public ResponseEntity<List<VideoLyricLineDTO>> importLyrics(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("Uploaded file must not be empty");
		}

		return ResponseEntity.ok(videoLyricLineService.importFromSrt(file));
	}

	@GetMapping("/{videoId}/lyric")
	@Operation(summary = "Get all lyrics for a video", description = "Returns a list of all lyric lines for a video")
	@ApiResponse(responseCode = "200", description = "Lyrics fetched successfully")
	public ResponseEntity<List<VideoLyricLine>> getAllLyrics(
			@PathVariable Long videoId,
			@RequestParam(defaultValue = "lineOrder") String sortBy,
			@RequestParam(defaultValue = "asc") String direction,
			@RequestParam(required = false) String keyword) {

		return ResponseEntity.ok(videoLyricLineService.findAll(videoId, sortBy, direction, keyword));
	}

	@GetMapping("/lyric/{lyricId}")
	@Operation(summary = "Get a single lyric line", description = "Returns a single lyric line by ID")
	@ApiResponse(responseCode = "200", description = "Lyric line fetched successfully")
	public ResponseEntity<VideoLyricLine> getLyric(@PathVariable Long lyricId) {
		return ResponseEntity.ok(videoLyricLineService.findById(lyricId));
	}

	@PostMapping("/{videoId}/lyric/batch")
	@Operation(summary = "Create multiple lyric lines", description = "Create multiple lyrics for a video in a single request")
	@ApiResponse(responseCode = "201", description = "Lyric lines created successfully")
	public ResponseEntity<List<VideoLyricLine>> createMultipleLyrics(
			@PathVariable Long videoId,
			@Valid @RequestBody List<VideoLyricLineDTO> videoLyricLineDTOs) {

		return ResponseEntity.status(201).body(videoLyricLineService.createBatch(videoId, videoLyricLineDTOs));
	}

	@PostMapping("/{videoId}/lyric")
	@Operation(summary = "Create a single lyric line", description = "Create a single lyric line for a video")
	@ApiResponse(responseCode = "201", description = "Lyric line created successfully")
	public ResponseEntity<VideoLyricLine> createLyric(
			@PathVariable Long videoId,
			@Valid @RequestBody VideoLyricLineDTO videoLyricLineDTO) {

		return ResponseEntity.status(201).body(videoLyricLineService.createLyric(videoId, videoLyricLineDTO));
	}

	@PutMapping("/lyric/{lyricId}")
	@Operation(summary = "Update a lyric line", description = "Update an existing lyric line by ID")
	@ApiResponse(responseCode = "200", description = "Lyric line updated successfully")
	public ResponseEntity<VideoLyricLine> updateLyric(
			@PathVariable Long lyricId,
			@Valid @RequestBody VideoLyricLineDTO videoLyricLineDTO) {

		return ResponseEntity.ok(videoLyricLineService.updateLyric(lyricId, videoLyricLineDTO));
	}

	@DeleteMapping("/lyric/{lyricId}")
	@Operation(summary = "Delete a lyric line", description = "Delete a single lyric line by ID")
	@ApiResponse(responseCode = "204", description = "Lyric line deleted successfully")
	public ResponseEntity<Void> deleteLyric(@PathVariable Long lyricId) {
		videoLyricLineService.deleteLyric(lyricId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{videoId}/lyric")
	@Operation(summary = "Delete all lyrics for a video", description = "Remove all lyric lines associated with a video")
	@ApiResponse(responseCode = "204", description = "All lyric lines deleted successfully")
	public ResponseEntity<Void> deleteAllLyrics(@PathVariable Long videoId) {
		videoLyricLineService.deleteAllLyrics(videoId);
		return ResponseEntity.noContent().build();
	}
}
