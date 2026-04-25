package com.todaii.english.server.toeic_question_group;

import com.todaii.english.core.entity.ToeicQuestionGroup;
import com.todaii.english.core.entity.ToeicTest;
import com.todaii.english.server.toeic_test.TestRepository;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.dto.ToeicQuestionGroupDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionGroupService {

    private final QuestionGroupRepository questionGroupRepository;
    private final TestRepository testRepository;
    private final ModelMapper modelMapper;

    public Page<ToeicQuestionGroupDTO> getAllPaged(Long testId, Pageable pageable) {

        Page<ToeicQuestionGroup> page;

        if (testId != null) {
            page = questionGroupRepository.findByTestId(testId, pageable);
        } else {
            page = questionGroupRepository.findAll(pageable);
        }

        return page.map(this::toDTO);
    }

    public ToeicQuestionGroupDTO getById(Long id){
        return toDTO(findById(id));
    }

    public ToeicQuestionGroup findById(Long id){
        return questionGroupRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Question group not found"));
    }

    public ToeicQuestionGroup create(ToeicQuestionGroupDTO dto){
        ToeicQuestionGroup group = modelMapper.map(dto, ToeicQuestionGroup.class);
        setTest(group, dto.getTestId());
        return questionGroupRepository.save(group);
    }

    public ToeicQuestionGroup update(Long id, ToeicQuestionGroupDTO dto) {
        ToeicQuestionGroup group = findById(id);
        modelMapper.map(dto, group);
        setTest(group, dto.getTestId());
        return questionGroupRepository.save(group);
    }

    public void delete(Long id) {
        ToeicQuestionGroup group = findById(id);
        questionGroupRepository.delete(group);
    }

    private void setTest(ToeicQuestionGroup group, Long testId) {
        if (testId != null) {
            ToeicTest test = testRepository.findById(testId)
                    .orElseThrow(() -> new BusinessException(404, "Test not found"));
            group.setTest(test);
        }
    }

    private ToeicQuestionGroupDTO toDTO(ToeicQuestionGroup entity) {
        ToeicQuestionGroupDTO dto = modelMapper.map(entity, ToeicQuestionGroupDTO.class);

        if (entity.getTest() != null) {
            dto.setTestId(entity.getTest().getId());
            dto.setTestTitle(entity.getTest().getTitle());
        }

        return dto;
    }
}


