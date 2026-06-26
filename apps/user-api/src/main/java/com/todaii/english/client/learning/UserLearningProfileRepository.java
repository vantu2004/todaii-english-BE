package com.todaii.english.client.learning;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.learning.UserLearningProfile;

@Repository
public interface UserLearningProfileRepository extends JpaRepository<UserLearningProfile, Long> {
  Optional<UserLearningProfile> findByUserId(Long userId);
}
