package com.todaii.english.server.topic;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.Topic;
import com.todaii.english.shared.request.server.CreateTopicRequest;
import com.todaii.english.shared.response.PagedResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/topic")
@Tag(name = "Topic", description = "Endpoints for managing topics")
public class TopicApiController {

    private final TopicService topicService;

    @Operation(summary = "Get all topics (paged)", description = "Retrieve paginated topics with optional search keyword and topic type filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved topics",
                    content = @Content(schema = @Schema(
                            example = "{\n" +
                                    "  \"content\": [\n" +
                                    "    {\n" +
                                    "      \"id\": 1,\n" +
                                    "      \"name\": \"Basic English\",\n" +
                                    "      \"enabled\": true,\n" +
                                    "      \"topicType\": \"VOCABULARY\",\n" +
                                    "      \"createdAt\": \"2025-11-01T12:00:00\",\n" +
                                    "      \"updatedAt\": \"2025-11-15T09:00:00\"\n" +
                                    "    }\n" +
                                    "  ],\n" +
                                    "  \"page\": 1,\n" +
                                    "  \"size\": 10,\n" +
                                    "  \"totalElements\": 50,\n" +
                                    "  \"totalPages\": 5,\n" +
                                    "  \"first\": true,\n" +
                                    "  \"last\": false,\n" +
                                    "  \"sortBy\": \"id\",\n" +
                                    "  \"direction\": \"desc\"\n" +
                                    "}"))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(schema = @Schema(
                            example = "{\n" +
                                    "  \"timestamp\": \"2025-11-29T07:44:59.347Z\",\n" +
                                    "  \"status\": 400,\n" +
                                    "  \"path\": \"/api/v1/topic\",\n" +
                                    "  \"errors\": [\"Page must be at least 1\"]\n" +
                                    "}")))
    })
    @GetMapping
    public ResponseEntity<PagedResponse<Topic>> getAllTopicsPaged(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam @NotNull String topicType) {

        Page<Topic> topics = topicService.findAllPaged(page, size, sortBy, direction, keyword, topicType);

        PagedResponse<Topic> response = new PagedResponse<>(
                topics.getContent(),
                page,
                size,
                topics.getTotalElements(),
                topics.getTotalPages(),
                topics.isFirst(),
                topics.isLast(),
                sortBy,
                direction
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get topic by ID", description = "Retrieve a single topic by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Topic found",
                    content = @Content(schema = @Schema(
                            example = "{\n" +
                                    "  \"id\": 1,\n" +
                                    "  \"name\": \"Basic English\",\n" +
                                    "  \"enabled\": true,\n" +
                                    "  \"topicType\": \"VOCABULARY\",\n" +
                                    "  \"createdAt\": \"2025-11-01T12:00:00\",\n" +
                                    "  \"updatedAt\": \"2025-11-15T09:00:00\"\n" +
                                    "}"))),
            @ApiResponse(responseCode = "404", description = "Topic not found",
                    content = @Content(schema = @Schema(
                            example = "{\n" +
                                    "  \"timestamp\": \"2025-11-29T07:44:59.347Z\",\n" +
                                    "  \"status\": 404,\n" +
                                    "  \"path\": \"/api/v1/topic/99\",\n" +
                                    "  \"errors\": [\"Topic not found\"]\n" +
                                    "}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Topic> getTopic(@Parameter(description = "ID of the topic to retrieve") @PathVariable Long id) {
        return ResponseEntity.ok(topicService.findById(id));
    }

    @Operation(summary = "Create topic", description = "Create a new topic")
    @ApiResponse(responseCode = "201", description = "Topic created successfully",
            content = @Content(schema = @Schema(
                    example = "{\n" +
                            "  \"id\": 51,\n" +
                            "  \"name\": \"Advanced English\",\n" +
                            "  \"enabled\": true,\n" +
                            "  \"topicType\": \"GRAMMAR\",\n" +
                            "  \"createdAt\": \"2025-11-29T08:00:00\",\n" +
                            "  \"updatedAt\": \"2025-11-29T08:00:00\"\n" +
                            "}")))
    @PostMapping
    public ResponseEntity<Topic> createTopic(@Valid @RequestBody CreateTopicRequest request) {
        return ResponseEntity.status(201).body(topicService.create(request));
    }

    @Operation(summary = "Update topic", description = "Update topic name by ID")
    @ApiResponse(responseCode = "200", description = "Topic updated successfully",
            content = @Content(schema = @Schema(
                    example = "{\n" +
                            "  \"id\": 1,\n" +
                            "  \"name\": \"Updated Topic Name\",\n" +
                            "  \"enabled\": true,\n" +
                            "  \"topicType\": \"VOCABULARY\",\n" +
                            "  \"createdAt\": \"2025-11-01T12:00:00\",\n" +
                            "  \"updatedAt\": \"2025-11-29T09:00:00\"\n" +
                            "}")))
    @PutMapping("/{id}")
    public ResponseEntity<Topic> updateTopic(
            @Parameter(description = "ID of the topic to update") @PathVariable Long id,
            @RequestParam @NotBlank String name) {
        return ResponseEntity.ok(topicService.update(id, name));
    }

    @Operation(summary = "Toggle topic enabled status", description = "Enable or disable a topic by ID")
    @ApiResponse(responseCode = "200", description = "Topic status toggled")
    @PatchMapping("/{id}/enabled")
    public ResponseEntity<Void> toggleEnabled(@Parameter(description = "ID of the topic to enable/disable") @PathVariable Long id) {
        topicService.toggleEnabled(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete topic", description = "Soft delete a topic by ID")
    @ApiResponse(responseCode = "204", description = "Topic deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@Parameter(description = "ID of the topic to delete") @PathVariable Long id) {
        topicService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
