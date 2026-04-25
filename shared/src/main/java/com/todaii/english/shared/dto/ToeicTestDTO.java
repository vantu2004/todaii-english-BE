package com.todaii.english.shared.dto;

import com.todaii.english.shared.enums.TestStatus;
import com.todaii.english.shared.enums.TestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


@Data
public class ToeicTestDTO {

    @NotNull(message = "collectionId is required")
    private Long collectionId;

    @NotBlank(message = "Collection name cannot be blank")
    @Length(max = 512, message = "Title must not exceed 192 characters")
    private String collectionName;

    @NotBlank(message = "Title cannot be blank")
    @Length(max = 512, message = "Title must not exceed 512 characters")
    private String title;

    @NotBlank(message = "Test type cannot be blank")
    private TestType testType;

    @NotBlank(message = "Duration cannot be blank")
    private Integer duration;

    @NotBlank(message = "Audio url cannot be blank")
    @Length(max = 512, message = "Audio url must not exceed 512 characters")
    private String audioUrl;

    @NotBlank(message = "Thumbnail cannot be blank")
    @Length(max = 512, message = "Thumbnail must not exceed 512 characters")
    private String thumbnail;

    @NotBlank(message = "Description cannot be blank")
    @Length(max = 512, message = "Description must not exceed 1024 characters")
    private String description;

    @NotBlank(message = "Status cannot be blank")
    private TestStatus status;

}