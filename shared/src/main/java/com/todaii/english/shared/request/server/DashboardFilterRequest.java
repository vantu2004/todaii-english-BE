package com.todaii.english.shared.request.server;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.UsageType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardFilterRequest {
  @NotNull(message = "Start date is required")
  private LocalDate startDate;

  @NotNull(message = "End date is required")
  private LocalDate endDate;

  @NotNull(message = "Actor type is required")
  private ActorType actorType;

  @NotNull(message = "Usage type is required")
  private UsageType usageType;
}
