package com.todaii.english.server.toeic_question_group;

import com.todaii.english.core.entity.ToeicQuestionGroup;
import com.todaii.english.shared.dto.ToeicQuestionGroupDTO;
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
@RequestMapping("/api/v1/toeic/question-group")
public class QuestionGroupController {
    private  final QuestionGroupService questionGroupService;

    @GetMapping
    public ResponseEntity<Page<ToeicQuestionGroupDTO>> getAllPaged(
            @RequestParam(required = false) Long testId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(questionGroupService.getAllPaged(testId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToeicQuestionGroupDTO> getById(@PathVariable Long id) {
        ToeicQuestionGroupDTO entity = questionGroupService.getById(id);
        return ResponseEntity.ok(entity);
    }

    @PostMapping
    public ResponseEntity<ToeicQuestionGroup> createQuestionGroup(@Valid @RequestBody ToeicQuestionGroupDTO dto) {
        return ResponseEntity.status(201).body(questionGroupService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ToeicQuestionGroup> updateQuestionGroup(@PathVariable Long id, @Valid @RequestBody ToeicQuestionGroupDTO dto){
        return ResponseEntity.ok(questionGroupService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestionGroup(@PathVariable Long id) {
        questionGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
