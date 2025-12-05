package com.todaii.english.server.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.server.AdminUtils;
import com.todaii.english.shared.dto.UserDTO;
import com.todaii.english.shared.request.server.UpdateUserRequest;
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
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "Endpoints for managing client users")
public class UserApiController {

    private final UserService userService;

    @Operation(summary = "Get all users (paged)", description = "Retrieve paginated users with optional search keyword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users",
                    content = @Content(schema = @Schema(
                            example = "{\n" +
                                    "  \"content\": [\n" +
                                    "    {\n" +
                                    "      \"id\": 1,\n" +
                                    "      \"email\": \"user1@example.com\",\n" +
                                    "      \"displayName\": \"John Doe\",\n" +
                                    "      \"avatarUrl\": \"https://example.com/avatar1.png\",\n" +
                                    "      \"enabled\": true,\n" +
                                    "      \"status\": \"ACTIVE\",\n" +
                                    "      \"emailVerifiedAt\": \"2025-11-29T07:00:00\",\n" +
                                    "      \"lastLoginAt\": \"2025-11-29T07:30:00\",\n" +
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
                                    "  \"path\": \"/api/v1/user\",\n" +
                                    "  \"errors\": [\"Page must be at least 1\"]\n" +
                                    "}")))
    })
    @GetMapping
    public ResponseEntity<PagedResponse<UserDTO>> getAllUsersPaged(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword) {

        Page<UserDTO> userDTOs = userService.findAllPaged(page, size, sortBy, direction, keyword);

        PagedResponse<UserDTO> response = new PagedResponse<>(
                userDTOs.getContent(),
                page,
                size,
                userDTOs.getTotalElements(),
                userDTOs.getTotalPages(),
                userDTOs.isFirst(),
                userDTOs.isLast(),
                sortBy,
                direction
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a single user by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(
                            implementation = UserDTO.class,
                            example = "{\n" +
                                    "  \"id\": 1,\n" +
                                    "  \"email\": \"user1@example.com\",\n" +
                                    "  \"displayName\": \"John Doe\",\n" +
                                    "  \"avatarUrl\": \"https://example.com/avatar1.png\",\n" +
                                    "  \"enabled\": true,\n" +
                                    "  \"status\": \"ACTIVE\",\n" +
                                    "  \"emailVerifiedAt\": \"2025-11-29T07:00:00\",\n" +
                                    "  \"lastLoginAt\": \"2025-11-29T07:30:00\",\n" +
                                    "  \"createdAt\": \"2025-11-01T12:00:00\",\n" +
                                    "  \"updatedAt\": \"2025-11-15T09:00:00\"\n" +
                                    "}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(
                            example = "{\n" +
                                    "  \"timestamp\": \"2025-11-29T07:44:59.347Z\",\n" +
                                    "  \"status\": 404,\n" +
                                    "  \"path\": \"/api/v1/user/99\",\n" +
                                    "  \"errors\": [\"User not found\"]\n" +
                                    "}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@Parameter(description = "ID of the user to retrieve") @PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserDTOById(id));
    }

	@PutMapping("/{id}")
	public ResponseEntity<UserDTO> updateUser(Authentication authentication, @PathVariable Long id,
			@Valid @RequestBody UpdateUserRequest updateUserRequest) {
		Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);

		return ResponseEntity.ok(userService.update(currentAdminId, id, updateUserRequest));
	}

	@PatchMapping("/{id}/enabled")
	public ResponseEntity<Void> toggleEnabled(Authentication authentication, @PathVariable Long id) {
		Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);

		userService.toggleEnabled(currentAdminId, id);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(Authentication authentication, @PathVariable Long id) {
		Long currentAdminId = AdminUtils.getCurrentAdminId(authentication);

		userService.delete(currentAdminId, id);

		return ResponseEntity.noContent().build();
	}
}
