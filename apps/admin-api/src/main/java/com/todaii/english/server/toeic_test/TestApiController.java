package com.todaii.english.server.toeic_test;

import com.todaii.english.shared.dto.ToeicTestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/toeic/test")
public class TestApiController {
    private final TestService testService;

    // ===================== GET PAGED (with optional filter) =====================
    @GetMapping
    public Page<ToeicTestDTO> getPaged(
            @RequestParam(required = false) Long collectionId,
            Pageable pageable
    ) {
        return testService.getPaged(collectionId, pageable);
    }

    @GetMapping("/{id}")
    public ToeicTestDTO getById(@PathVariable Long id) {
        return testService.getById(id);
    }

    @PostMapping
    public ToeicTestDTO createTest(@RequestBody ToeicTestDTO dto) {
        return testService.create(dto);
    }

    public ToeicTestDTO updateTest(@PathVariable Long id, @RequestBody ToeicTestDTO dto) {
        return testService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteTest(@PathVariable Long id) {
        testService.delete(id);
    }
}
