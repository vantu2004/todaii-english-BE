package com.todaii.english.server.video;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.dnbn.submerge.api.parser.SRTParser;
import com.github.dnbn.submerge.api.subtitle.srt.SRTLine;
import com.github.dnbn.submerge.api.subtitle.srt.SRTSub;
import com.todaii.english.core.entity.Video;
import com.todaii.english.core.entity.VideoLyricLine;
import com.todaii.english.shared.dto.VideoLyricLineDTO;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.utils.DateTimeUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoLyricLineService {
	private final VideoRepository videoRepository;
	private final VideoLyricLineRepository videoLyricLineRepository;
	private final ModelMapper modelMapper;

	public List<VideoLyricLineDTO> importFromSrt(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new RuntimeException("Uploaded file is empty");
		}

		try {
			File tempFile = File.createTempFile("subtitle", ".srt");
			file.transferTo(tempFile);

			SRTParser parser = new SRTParser();
			SRTSub srt = parser.parse(tempFile);

			Set<SRTLine> lines = srt.getLines();
			List<VideoLyricLineDTO> result = new ArrayList<>();

			for (SRTLine line : lines) {

				List<String> parts = line.getTextLines();
				String textEn = parts.size() > 0 ? parts.get(0).trim() : "";
				String textVi = parts.size() > 1 ? parts.get(1).trim() : "";
				int startMs = DateTimeUtils.toMillis(line.getTime().getStart());
				int endMs = DateTimeUtils.toMillis(line.getTime().getEnd());

				result.add(VideoLyricLineDTO.builder().lineOrder(line.getId()).textEn(textEn).textVi(textVi)
						.startMs(startMs).endMs(endMs).build());
			}

			return result;

		} catch (Exception e) {
			throw new BusinessException(400, "Failed to parse SRT file.\nPlease check data file again");
		}
	}

	public List<VideoLyricLine> findAll(Long videoId, String sortBy, String direction, String keyword) {
		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);

		return videoLyricLineRepository.findAll(videoId, keyword, sort);
	}

	public VideoLyricLine findById(Long lyricId) {
		return videoLyricLineRepository.findById(lyricId)
				.orElseThrow(() -> new BusinessException(404, "Lyric not found in this video"));
	}

	public List<VideoLyricLine> createBatch(Long videoId, List<VideoLyricLineDTO> videoLyricLineDTOs) {
		Video video = videoRepository.findById(videoId)
				.orElseThrow(() -> new BusinessException(404, "Video not found"));

		List<VideoLyricLine> videoLyricLines = videoLyricLineDTOs.stream().map(dto -> {
			VideoLyricLine videoLyricLine = modelMapper.map(dto, VideoLyricLine.class);

			// Quan trọng: Thiết lập mối quan hệ với video cha
			videoLyricLine.setVideo(video);

			return videoLyricLine;
		}).collect(Collectors.toList());

		return videoLyricLineRepository.saveAll(videoLyricLines);
	}

	public VideoLyricLine createLyric(Long videoId, VideoLyricLineDTO videoLyricLineDTO) {
		Video video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Video not found"));

		VideoLyricLine videoLyricLine = modelMapper.map(videoLyricLineDTO, VideoLyricLine.class);
		videoLyricLine.setVideo(video);

		// Lưu và trả về
		return videoLyricLineRepository.save(videoLyricLine);
	}

	public VideoLyricLine updateLyric(Long lyricId, VideoLyricLineDTO videoLyricLineDTO) {
		VideoLyricLine videoLyricLine = findById(lyricId);

		modelMapper.map(videoLyricLineDTO, videoLyricLine);

		return videoLyricLineRepository.save(videoLyricLine);
	}

	@Transactional
	public void deleteLyric(Long lyricId) {
		if (!videoLyricLineRepository.existsById(lyricId)) {
			throw new BusinessException(404, "Lyric not found in this video");
		}

		videoLyricLineRepository.deleteById(lyricId);
	}

	@Transactional
	public void deleteAllLyrics(Long videoId) {
		videoLyricLineRepository.deleteAllByVideoId(videoId);
	}

}
