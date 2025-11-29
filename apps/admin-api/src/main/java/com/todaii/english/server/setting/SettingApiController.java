package com.todaii.english.server.setting;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.Setting;
import com.todaii.english.shared.enums.SettingCategory;
import com.todaii.english.shared.request.server.SettingRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/setting")
@Tag(name = "Settings", description = "Endpoints for managing settings")
public class SettingApiController {

    private final SettingService settingService;

    @Operation(summary = "Get settings by category", description = "Retrieve settings filtered by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved settings",
                    content = @Content(schema = @Schema(
                            example = "[\n" +
                                    "  {\n" +
                                    "    \"key\": \"app_language\",\n" +
                                    "    \"value\": \"en\",\n" +
                                    "    \"category\": \"GENERAL\",\n" +
                                    "    \"description\": \"Application default language\"\n" +
                                    "  },\n" +
                                    "  {\n" +
                                    "    \"key\": \"timezone\",\n" +
                                    "    \"value\": \"GMT+7\",\n" +
                                    "    \"category\": \"GENERAL\",\n" +
                                    "    \"description\": \"Default timezone\"\n" +
                                    "  }\n" +
                                    "]"
                    ))),
            @ApiResponse(responseCode = "400", description = "Invalid category",
                    content = @Content(schema = @Schema(
                            example = "{\n" +
                                    "  \"timestamp\": \"2025-11-29T08:30:00.000Z\",\n" +
                                    "  \"status\": 400,\n" +
                                    "  \"path\": \"/api/v1/setting\",\n" +
                                    "  \"errors\": [\"Category cannot be null\"]\n" +
                                    "}")))
    })
    @GetMapping
    public ResponseEntity<List<Setting>> getSettings(
            @Parameter(description = "Category to filter settings") @RequestParam SettingCategory category) {
        return ResponseEntity.ok(settingService.getSettingsByCategory(category));
    }

    @Deprecated
    public ResponseEntity<List<Setting>> updateSettings(@Valid @RequestBody SettingRequest request) {
        return ResponseEntity.ok(settingService.updateSettings(request));
    }

    @Operation(summary = "Update a single setting", description = "Update a specific setting by key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Setting updated successfully",
                    content = @Content(schema = @Schema(
                            example = "{\n" +
                                    "  \"key\": \"app_language\",\n" +
                                    "  \"value\": \"vi\",\n" +
                                    "  \"category\": \"GENERAL\",\n" +
                                    "  \"description\": \"Application default language\"\n" +
                                    "}"))),
            @ApiResponse(responseCode = "400", description = "Invalid value",
                    content = @Content(schema = @Schema(
                            example = "{\n" +
                                    "  \"timestamp\": \"2025-11-29T08:35:00.000Z\",\n" +
                                    "  \"status\": 400,\n" +
                                    "  \"path\": \"/api/v1/setting/app_language\",\n" +
                                    "  \"errors\": [\"Value cannot be blank\"]\n" +
                                    "}")))
    })
    @PutMapping("/{key}")
    public ResponseEntity<Setting> updateSetting(
            @Parameter(description = "Key of the setting to update") @PathVariable String key,
            @RequestParam @NotBlank(message = "Value cannot be blank")
            @Length(max = 1014, message = "Value must be less than 1024 characters") String value) {
        return ResponseEntity.ok(settingService.updateSetting(key, value));
    }
}
