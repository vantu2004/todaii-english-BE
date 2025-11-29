package com.todaii.english.server.gemini;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/gemini")
@Tag(
        name = "Gemini",
        description = "Endpoints for communicating with Google Gemini AI model"
)
public class GeminiApiController {

    private final GeminiService geminiService;

    @Operation(
            summary = "Generate AI response from Gemini",
            description = "Send a text prompt to the Gemini model and receive the generated response"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "AI response generated successfully",
                    content = @Content(
                            schema = @Schema(
                                    example = "This is the AI-generated response from Gemini..."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid prompt",
                    content = @Content(
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"timestamp\": \"2025-11-29T10:20:00.000Z\",\n" +
                                            "  \"status\": 400,\n" +
                                            "  \"path\": \"/api/v1/gemini/generate\",\n" +
                                            "  \"errors\": [\"Prompt must be between 1 and 1024 characters\"]\n" +
                                            "}"
                            )
                    )
            )
    })
    @GetMapping("/generate")
    public ResponseEntity<String> generate(
            @Parameter(description = "Text prompt for Gemini AI")
            @RequestParam
            @NotBlank(message = "Prompt must not be null")
            @Length(min = 1, max = 1024, message = "Prompt must be between 1 and 1024 characters")
            String prompt) {

        return ResponseEntity.ok(geminiService.askGemini(prompt));
    }
}
