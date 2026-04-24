package com.todaii.english.server.toeic_test;

import com.todaii.english.server.AdminUtils;
import com.todaii.english.shared.dto.ToeicTestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/toeic/test")
public class TestApiController {
    private final TestService testService;

    @GetMapping
    public ResponseEntity<Page<ToeicTestDTO>> getPaged(
            @RequestParam(required = false) Long collectionId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(testService.getPaged(collectionId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToeicTestDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(testService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ToeicTestDTO> createTest(Authentication authentication, @RequestBody ToeicTestDTO dto) {
        return ResponseEntity.status(201).body(testService.create(AdminUtils.getCurrentAdminId(authentication), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ToeicTestDTO> updateTest(@PathVariable Long id, @RequestBody ToeicTestDTO dto) {
        return ResponseEntity.ok(testService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable Long id) {
        testService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
