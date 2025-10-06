package com.todaii.english.server.video;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/video")
public class VideoApiController {
	private final VideoService videoService;
	
	
}
