package com.todaii.english.client.video;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.todaii.english.client.user.UserRepository;
import com.todaii.english.core.entity.User;
import com.todaii.english.core.entity.Video;
import com.todaii.english.shared.enums.CefrLevel;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoService {
	private final VideoRepository videoRepository;
	private final UserRepository userRepository;

	public List<Video> getLatestVideos(int size) {
		return videoRepository.findAllByEnabledTrueOrderByCreatedAtDesc(PageRequest.of(0, size)).getContent();
	}

	public List<Video> getTopVideos(int size) {
		return videoRepository.findAllByEnabledTrueOrderByViewsDesc(PageRequest.of(0, size)).getContent();
	}

	public Video findById(Long id) {
		Video video = videoRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Video not found"));
		video.setViews(video.getViews() + 1);

		return videoRepository.save(video);
	}

	public List<Video> getRelatedVideos(Long videoId, int limit) {
		Video video = videoRepository.findById(videoId)
				.orElseThrow(() -> new BusinessException(404, "Video not found"));

		List<Long> topicIds = video.getTopics().stream().map(t -> t.getId()).toList();

		List<Video> related = videoRepository.findRelatedByTopics(videoId, topicIds);

		if (related.isEmpty()) {
			return videoRepository.findFallbackByCefr(videoId, video.getCefrLevel(), PageRequest.of(0, limit));
		}

		if (related.size() < limit) {
			int remain = limit - related.size();
			List<Video> fallback = videoRepository.findFallbackByCefr(videoId, video.getCefrLevel(),
					PageRequest.of(0, remain));

			Set<Long> ids = related.stream().map(Video::getId).collect(Collectors.toSet());
			for (Video v : fallback) {
				if (!ids.contains(v.getId())) {
					related.add(v);
					ids.add(v.getId());
				}
				if (related.size() >= limit)
					break;
			}
		}

		Collections.shuffle(related);

		return related.size() > limit ? related.subList(0, limit) : related;
	}

	public Page<Video> filterVideos(String keyword, CefrLevel cefrLevel, Integer minViews, String alias, int page,
			int size, String sortBy, String direction) {

		Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);

		Pageable pageable = PageRequest.of(page - 1, size, sort);

		Specification<Video> spec = VideoSpecification.isEnabled().and(VideoSpecification.hasKeyword(keyword))
				.and(VideoSpecification.hasCefrLevel(cefrLevel)).and(VideoSpecification.hasMinViews(minViews))
				.and(VideoSpecification.hasTopic(alias));

		return videoRepository.findAll(spec, pageable);
	}

	public List<Video> getSavedVideosByUserId(Long currentUserId) {
		User user = userRepository.findById(currentUserId)
				.orElseThrow(() -> new BusinessException(404, "User not found"));
		Set<Video> savedSet = user.getSavedVideos();

		return new ArrayList<>(savedSet);
	}

	public Boolean isSavedByUser(Long videoId, Long currentUserId) {
		User user = userRepository.findById(currentUserId)
				.orElseThrow(() -> new BusinessException(404, "User not found"));

		return user.getSavedArticles().stream().anyMatch(a -> a.getId().equals(videoId));
	}

}
