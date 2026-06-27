package com.todaii.english.client.learning.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todaii.english.client.learning.repository.UserLearningProfileRepository;
import com.todaii.english.client.user.UserRepository;
import com.todaii.english.core.entity.learning.UserLearningProfile;
import com.todaii.english.core.entity.user.User;
import com.todaii.english.shared.dto.learning.UserLearningProfileDTO;
import com.todaii.english.shared.request.client.UpdateLearningProfileRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserLearningProfileService {
  private final UserLearningProfileRepository userLearningProfileRepository;
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;

  @Transactional(readOnly = true)
  public UserLearningProfileDTO getProfile(Long userId) {
    return modelMapper.map(
        userLearningProfileRepository.findByUserId(userId), UserLearningProfileDTO.class);
  }

  @Transactional
  public UserLearningProfileDTO updateProfile(Long userId, UpdateLearningProfileRequest request) {
    UserLearningProfile profile = getOrCreateProfile(userId);

    if (request.getTargetScore() != null) {
      profile.setTargetScore(request.getTargetScore());
    }
    if (request.getExamDate() != null) {
      profile.setExamDate(request.getExamDate());
    }

    UserLearningProfile saved = userLearningProfileRepository.save(profile);

    return modelMapper.map(saved, UserLearningProfileDTO.class);
  }

  private UserLearningProfile getOrCreateProfile(Long userId) {
    return userLearningProfileRepository
        .findByUserId(userId)
        .orElseGet(
            () -> {
              User user = userRepository.findById(userId).orElseThrow();
              UserLearningProfile newProfile =
                  UserLearningProfile.builder()
                      .user(user)
                      .targetScore(0) // Default target score
                      .currentScore(0)
                      .examDate(null)
                      .build();
              return userLearningProfileRepository.save(newProfile);
            });
  }
}
