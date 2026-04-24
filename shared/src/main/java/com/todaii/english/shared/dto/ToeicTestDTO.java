package com.todaii.english.shared.dto;

import com.todaii.english.shared.enums.TestStatus;
import com.todaii.english.shared.enums.TestType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ToeicTestDTO {
    private Long id;
    private Long collectionId;
    private String collectionName;
    private String title;
    private TestType testType;
    private Integer duration;
    private String audioUrl;
    private String thumbnail;
    private String description;
    private TestStatus status;
    private Long creatorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}