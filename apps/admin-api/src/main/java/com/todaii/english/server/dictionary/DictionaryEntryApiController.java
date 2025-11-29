package com.todaii.english.server.dictionary;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.shared.dto.DictionaryEntryDTO;
import com.todaii.english.shared.response.DictionaryApiResponse;
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
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/dictionary")
@Tag(
        name = "Dictionary",
        description = "Endpoints for managing the dictionary entries"
)
public class DictionaryEntryApiController {

    private final DictionaryEntryService dictionaryService;

    // ---------------------------
    // RAW WORD LOOKUP
    // ---------------------------
    @Operation(
            summary = "Lookup raw dictionary info",
            description = "Query external API for raw dictionary definitions of a word"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Dictionary raw info retrieved successfully",
            content = @Content(
                    schema = @Schema(implementation = DictionaryApiResponse[].class),
                    examples = {
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "[{\"word\": \"hello\", \"phonetic\": \"/həˈləʊ/\", \"meanings\": []}]"
                            )
                    }
            )
    )
    @GetMapping("/raw-word")
    public ResponseEntity<DictionaryApiResponse[]> getRawWord(
            @Parameter(description = "Word to lookup")
            @RequestParam String word) {

        DictionaryApiResponse[] dictionaryApiResponses = dictionaryService.lookupWord(word);
        return ResponseEntity.ok(dictionaryApiResponses);
    }

    // ---------------------------
    // PAGED LIST
    // ---------------------------
    @Operation(summary = "Get paginated dictionary entries")
    @ApiResponse(
            responseCode = "200",
            description = "Paged dictionary entries retrieved",
            content = @Content(
                    schema = @Schema(implementation = PagedResponse.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = "{\n" +
                                    "  \"content\": [\n" +
                                    "    {\n" +
                                    "      \"id\": 1,\n" +
                                    "      \"headword\": \"hello\",\n" +
                                    "      \"pronunciation\": \"həˈləʊ\",\n" +
                                    "      \"definition\": \"A greeting\",\n" +
                                    "      \"exampleSentence\": \"Hello, how are you?\"\n" +
                                    "    }\n" +
                                    "  ],\n" +
                                    "  \"page\": 1,\n" +
                                    "  \"size\": 20,\n" +
                                    "  \"totalElements\": 50,\n" +
                                    "  \"totalPages\": 3,\n" +
                                    "  \"first\": true,\n" +
                                    "  \"last\": false,\n" +
                                    "  \"sortBy\": \"headword\",\n" +
                                    "  \"direction\": \"asc\"\n" +
                                    "}"
                    )
            )
    )
    @GetMapping
    public ResponseEntity<PagedResponse<DictionaryEntry>> getAllPaged(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(defaultValue = "headword") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String keyword) {

        Page<DictionaryEntry> entries = dictionaryService.findAllPaged(page, size, sortBy, direction, keyword);

        PagedResponse<DictionaryEntry> response = new PagedResponse<>(
                entries.getContent(),
                page,
                size,
                entries.getTotalElements(),
                entries.getTotalPages(),
                entries.isFirst(),
                entries.isLast(),
                sortBy,
                direction
        );

        return ResponseEntity.ok(response);
    }

    // ---------------------------
    // GET SINGLE ENTRY
    // ---------------------------
    @Operation(summary = "Get dictionary entry by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Entry found",
                    content = @Content(schema = @Schema(implementation = DictionaryEntry.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Entry not found",
                    content = @Content(
                            schema = @Schema(
                                    example = "{\n" +
                                            "  \"timestamp\": \"2025-11-29T10:20:00.000Z\",\n" +
                                            "  \"status\": 404,\n" +
                                            "  \"path\": \"/api/v1/dictionary/10\",\n" +
                                            "  \"errors\": [\"Dictionary entry not found\"]\n" +
                                            "}"
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<DictionaryEntry> getWord(@PathVariable Long id) {
        return ResponseEntity.ok(dictionaryService.findById(id));
    }

    // ---------------------------
    // CREATE VIA GEMINI
    // ---------------------------
    @Operation(summary = "Create dictionary entry using Gemini AI")
    @PostMapping("/gemini")
    public ResponseEntity<List<DictionaryEntry>> createWordByGemini(
            @Parameter(description = "Word to generate definition for")
            @RequestParam @NotBlank @Length(max = 64) String word) throws Exception {

        return ResponseEntity.ok(dictionaryService.createWordByGemini(word));
    }

    // ---------------------------
    // CREATE MANUALLY
    // ---------------------------
    @Operation(summary = "Create dictionary entry manually")
    @PostMapping
    public ResponseEntity<DictionaryEntry> createWord(
            @Valid @RequestBody DictionaryEntryDTO dictionaryEntryDTO) {

        return ResponseEntity.status(201).body(dictionaryService.createWord(dictionaryEntryDTO));
    }

    // ---------------------------
    // UPDATE
    // ---------------------------
    @Operation(summary = "Update dictionary entry")
    @PutMapping("/{id}")
    public ResponseEntity<DictionaryEntry> updateWord(
            @PathVariable Long id,
            @Valid @RequestBody DictionaryEntryDTO dictionaryEntryDTO) {

        return ResponseEntity.ok(dictionaryService.updateWord(id, dictionaryEntryDTO));
    }

    // ---------------------------
    // DELETE
    // ---------------------------
    @Operation(summary = "Delete dictionary entry")
    @ApiResponse(responseCode = "204", description = "Entry deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
        dictionaryService.deleteWord(id);
        return ResponseEntity.noContent().build();
    }
}
