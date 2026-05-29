package com.todaii.english.server.dashboard;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.UsageStatistic;
import com.todaii.english.shared.enums.ActorType;

@Repository
public interface DashboardRepository extends JpaRepository<UsageStatistic, Long> {

  // Lấy toàn bộ trong khoảng thời gian (phục vụ biểu đồ tổng/top)
  List<UsageStatistic> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate);

  // Lấy theo loại Actor (phục vụ /admin-chart, /user-chart, /guest-chart)
  List<UsageStatistic> findByActorTypeAndCreatedAtBetween(
      ActorType actorType, LocalDate startDate, LocalDate endDate);

  // Lấy theo cụ thể 1 User/Admin (phục vụ /admin-chart/{id}, /user-chart/{id})
  List<UsageStatistic> findByActorTypeAndActorIdAndCreatedAtBetween(
      ActorType actorType, Long actorId, LocalDate startDate, LocalDate endDate);
}
