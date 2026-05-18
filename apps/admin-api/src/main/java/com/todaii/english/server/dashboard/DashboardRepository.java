package com.todaii.english.server.dashboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.UsageStatistic;

@Repository
public interface DashboardRepository extends JpaRepository<UsageStatistic, Long> {}
