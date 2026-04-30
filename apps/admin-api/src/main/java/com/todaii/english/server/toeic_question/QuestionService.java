package com.todaii.english.server.toeic_question;

import com.todaii.english.core.entity.ToeicQuestion;
import com.todaii.english.core.entity.ToeicQuestionGroup;
import com.todaii.english.core.entity.ToeicTag;
import com.todaii.english.core.entity.ToeicTest;
import com.todaii.english.server.toeic_question_group.QuestionGroupRepository;
import com.todaii.english.server.toeic_tag.TagRepository;
import com.todaii.english.server.toeic_test.TestRepository;
import com.todaii.english.shared.dto.ToeicQuestionDTO;
import com.todaii.english.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TagRepository tagRepository;
    private final TestRepository testRepository;
    private final QuestionGroupRepository groupRepository;
    private final ModelMapper modelMapper;

    public Page<ToeicQuestionDTO> getAllPaged(Long testId, Long groupId, List<Long> tagIds ,Pageable pageable) {

        Specification<ToeicQuestion> spec = (root, query, cb) -> cb.conjunction();

        if (testId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("test").get("id"), testId));
        }

        if (groupId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("group").get("id"), groupId));
        }

        if (tagIds != null && !tagIds.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                query.distinct(true);

                return root.join("tags").get("id").in(tagIds);
            });
        }

        Page<ToeicQuestion> page = questionRepository.findAll(spec, pageable);

        return page.map(this::toDTO);
    }

    public ToeicQuestionDTO getById(Long id) {
        return toDTO(findById(id));
    }

    public ToeicQuestion findById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Question not found"));
    }

    public ToeicQuestion create(ToeicQuestionDTO dto) {
        ToeicQuestion question = modelMapper.map(dto, ToeicQuestion.class);
        setRelations(question, dto);
        return questionRepository.save(question);
    }

    public ToeicQuestion update(Long id, ToeicQuestionDTO dto) {
        dto.setId(id);
        ToeicQuestion question = findById(id);
        modelMapper.map(dto, question);
        setRelations(question, dto);
        return questionRepository.save(question);
    }

    public void delete(Long id) {
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

        if (dto.getTagIds() != null) {
            List<ToeicTag> tags = tagRepository.findAllById(dto.getTagIds());

            if (tags.size() != dto.getTagIds().size()) {
                throw new BusinessException(400, "Some question tags not found");
            }

            question.setTags(Set.copyOf(tags));
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

        if (entity.getTags() != null) {
            dto.setTagIds(entity.getTags().stream()
                            .map(ToeicTag::getId)
                            .collect(Collectors.toSet()));
        }

        return dto;
    }
}
