package com.todaii.english.server.toeic_question;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todaii.english.core.entity.ToeicPassage;
import com.todaii.english.core.entity.ToeicQuestion;
import com.todaii.english.core.entity.ToeicTag;
import com.todaii.english.core.entity.ToeicTest;
import com.todaii.english.server.toeic_passage.PassageRepository;
import com.todaii.english.server.toeic_tag.TagRepository;
import com.todaii.english.server.toeic_test.TestRepository;
import com.todaii.english.shared.dto.ToeicQuestionDTO;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {

  private final QuestionRepository questionRepository;
  private final TagRepository tagRepository;
  private final TestRepository testRepository;
  private final PassageRepository groupRepository;
  private final ModelMapper modelMapper;

  public Page<ToeicQuestionDTO> getAllPaged(
      Long testId, Long passageId, List<Long> tagIds, Pageable pageable) {

    if (testId != null && !testRepository.existsById(testId)) {
      throw new BusinessException(404, "Test not found");
    }

    if (passageId != null && !groupRepository.existsById(passageId)) {
      throw new BusinessException(404, "Passage not found");
    }

    if (tagIds != null && !tagIds.isEmpty()) {

      List<ToeicTag> tags = tagRepository.findAllById(tagIds);

      if (tags.size() != tagIds.size()) {
        throw new BusinessException(404, "Some tags not found");
      }
    }

    Specification<ToeicQuestion> spec = (root, query, cb) -> cb.conjunction();

    if (testId != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("test").get("id"), testId));
    }

    if (passageId != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("passage").get("id"), passageId));
    }

    if (tagIds != null && !tagIds.isEmpty()) {
      spec =
          spec.and(
              (root, query, cb) -> {
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
    return questionRepository
        .findById(id)
        .orElseThrow(() -> new BusinessException(404, "Question not found"));
  }

  public ToeicQuestionDTO create(ToeicQuestionDTO dto) {
    ToeicQuestion question = modelMapper.map(dto, ToeicQuestion.class);
    setRelations(question, dto);
    ToeicQuestion savedQuestion = questionRepository.save(question);

    return toDTO(savedQuestion);
  }

  @Transactional
  public List<ToeicQuestion> createBulk(List<ToeicQuestionDTO> dtos) {

    List<ToeicQuestion> questions = new ArrayList<>();

    for (ToeicQuestionDTO dto : dtos) {
      ToeicQuestion question = modelMapper.map(dto, ToeicQuestion.class);

      ToeicTest test =
          testRepository
              .findById(dto.getTestId())
              .orElseThrow(() -> new BusinessException(404, "Test not found"));

      question.setTest(test);

      questions.add(question);
    }

    return questionRepository.saveAll(questions);
  }

  public ToeicQuestion update(Long id, ToeicQuestionDTO dto) {
    dto.setId(id);
    ToeicQuestion question = findById(id);

    modelMapper.map(dto, question);

    setRelations(question, dto);
    return questionRepository.save(question);
  }

  @Transactional
  public List<ToeicQuestion> updateBulk(List<ToeicQuestionDTO> dtos) {

    List<ToeicQuestion> questions = new ArrayList<>();

    for (ToeicQuestionDTO dto : dtos) {

      ToeicQuestion question =
          questionRepository
              .findById(dto.getId())
              .orElseThrow(
                  () ->
                      new BusinessException(
                          404, String.format("Question %d not found", dto.getId())));

      modelMapper.map(dto, question);

      questions.add(question);
    }

    return questionRepository.saveAll(questions);
  }

  public void delete(Long id) {
    questionRepository.delete(findById(id));
  }

  private void setRelations(ToeicQuestion question, ToeicQuestionDTO dto) {

    if (dto.getTestId() != null) {
      ToeicTest test =
          testRepository
              .findById(dto.getTestId())
              .orElseThrow(() -> new BusinessException(404, "Test not found"));
      question.setTest(test);
    }

    if (dto.getPassageId() != null) {
      ToeicPassage passage =
          groupRepository
              .findById(dto.getPassageId())
              .orElseThrow(() -> new BusinessException(404, "Passage not found"));
      question.setPassage(passage);
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

    if (entity.getPassage() != null) {
      dto.setPassageId(entity.getPassage().getId());
    }

    if (entity.getTags() != null) {
      dto.setTagIds(entity.getTags().stream().map(ToeicTag::getId).collect(Collectors.toSet()));
    }

    return dto;
  }
}
