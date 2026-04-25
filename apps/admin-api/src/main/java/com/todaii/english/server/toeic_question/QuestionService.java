package com.todaii.english.server.toeic_question;

import com.todaii.english.core.entity.ToeicQuestion;
import com.todaii.english.core.entity.ToeicQuestionGroup;
import com.todaii.english.core.entity.ToeicTest;
import com.todaii.english.server.toeic_question_group.QuestionGroupRepository;
import com.todaii.english.server.toeic_test.TestRepository;
import com.todaii.english.shared.dto.ToeicQuestionDTO;
import com.todaii.english.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final QuestionGroupRepository groupRepository;
    private final ModelMapper modelMapper;

    public Page<ToeicQuestionDTO> getAllPaged(Long testId, Long groupId, Pageable pageable) {

        Page<ToeicQuestion> page;

        if (groupId != null) {
            page = questionRepository.findByGroupId(groupId, pageable);
        } else if (testId != null) {
            page = questionRepository.findByTestId(testId, pageable);
        } else {
            page = questionRepository.findAll(pageable);
        }

        return page.map(this::toDTO);
    }

    public ToeicQuestionDTO getById(Long id) {
        return toDTO(findById(id));
    }

    public ToeicQuestion findById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Question not found"));
    }

    public ToeicQuestion createQuestion(ToeicQuestionDTO dto) {
        ToeicQuestion question = modelMapper.map(dto, ToeicQuestion.class);
        setRelations(question, dto);
        return questionRepository.save(question);
    }

    public ToeicQuestion updateQuestion(Long id, ToeicQuestionDTO dto) {
        ToeicQuestion question = findById(id);
        modelMapper.map(dto, question);
        setRelations(question, dto);
        return questionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        questionRepository.delete(findById(id));
    }

    private void setRelations(ToeicQuestion question, ToeicQuestionDTO dto) {

        if (dto.getTestId() != null) {
            ToeicTest test = testRepository.findById(dto.getTestId())
                    .orElseThrow(() -> new BusinessException(404, "Test not found"));
            question.setTest(test);
        }

        if (dto.getGroupId() != null) {
            ToeicQuestionGroup group = groupRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new BusinessException(404, "Group not found"));
            question.setGroup(group);
        }
    }

    private ToeicQuestionDTO toDTO(ToeicQuestion entity) {
        ToeicQuestionDTO dto = modelMapper.map(entity, ToeicQuestionDTO.class);

        if (entity.getTest() != null) {
            dto.setTestId(entity.getTest().getId());
        }

        if (entity.getGroup() != null) {
            dto.setGroupId(entity.getGroup().getId());
        }

        return dto;
    }
}
