package com.todaii.english.server.toeic_question;

import com.todaii.english.core.entity.ToeicQuestion;
import com.todaii.english.shared.dto.ToeicQuestionDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/toeic/question")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<Page<ToeicQuestionDTO>> getAll(
            @RequestParam(required = false) Long testId,
            @RequestParam(required = false) Long groupId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(questionService.getAllPaged(testId, groupId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToeicQuestionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ToeicQuestion> create(@Valid @RequestBody ToeicQuestionDTO dto) {
        return ResponseEntity.status(201).body(questionService.createQuestion(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ToeicQuestion> update(
            @PathVariable Long id,
            @Valid @RequestBody ToeicQuestionDTO dto
    ) {
        return ResponseEntity.ok(questionService.updateQuestion(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
