package com.todaii.english.client.user;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  public Optional<User> findByEmail(String email);

  @Query("SELECT u FROM User u WHERE u.email = ?1 AND u.isDeleted = false")
  public Optional<User> findActiveByEmail(String email);

  @Query("SELECT u FROM User u WHERE u.resetPasswordToken = ?1 AND u.isDeleted = false")
  public Optional<User> findByResetPasswordToken(String token);

  @Query("SELECT u FROM User u WHERE u.id = ?1 AND u.isDeleted = false")
  public Optional<User> findById(Long id);

  // Streak Risk: user hôm nay chưa học nhưng đang có streak > 0
  List<User> findByCurrentStreakGreaterThanAndLastStudyDateBefore(int streak, LocalDate date);

  // Churn Alert: user bỏ học đúng N ngày
  List<User> findByLastStudyDate(LocalDate date);
}
