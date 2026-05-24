package com.todaii.english.client.toeic_test_session;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todaii.english.client.toeic_question.QuestionRepository;
import com.todaii.english.client.toeic_test.TestRepository;
import com.todaii.english.client.user.UserRepository;
import com.todaii.english.core.entity.toeic.ToeicQuestion;
import com.todaii.english.core.entity.toeic.ToeicTest;
import com.todaii.english.core.entity.toeic.ToeicTestSession;
import com.todaii.english.core.entity.toeic.ToeicUserAnswer;
import com.todaii.english.core.entity.user.User;
import com.todaii.english.shared.dto.ToeicTestSessionDTO;
import com.todaii.english.shared.dto.ToeicUserAnswerDTO;
import com.todaii.english.shared.enums.ToeicSessionMode;
import com.todaii.english.shared.enums.ToeicSessionStatus;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.client.AnswerRequest;
import com.todaii.english.shared.request.client.StartSessionRequest;
import com.todaii.english.shared.request.client.SubmitSessionRequest;
import com.todaii.english.shared.response.PagedResponse;
import com.todaii.english.shared.response.ToeicSessionDetailsResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ToeicTestSessionService {
  private final ToeicTestSessionRepository sessionRepository;
  private final ToeicUserAnswerRepository userAnswerRepository;
  private final UserRepository userRepository;
  private final TestRepository testRepository;
  private final QuestionRepository questionRepository;

  @Transactional
  public ToeicTestSessionDTO startSession(Long userId, StartSessionRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BusinessException(404, "User not found"));

    ToeicTest test =
        testRepository
            .findById(request.getTestId())
            .orElseThrow(() -> new BusinessException(404, "TOEIC test not found"));

    ToeicTestSession session =
        ToeicTestSession.builder()
            .user(user)
            .test(test)
            .mode(request.getMode())
            .status(ToeicSessionStatus.IN_PROGRESS)
            .scoreL(0)
            .scoreR(0)
            .totalScore(0)
            .correctCount(0)
            .incorrectCount(0)
            .skippedCount(0)
            .timeSpent(0)
            .partsDone(
                request.getMode() == ToeicSessionMode.PRACTICE ? request.getPartsDone() : null)
            .startedAt(LocalDateTime.now())
            .build();

    session = sessionRepository.save(session);
    return mapToSessionDTO(session);
  }

  @Transactional
  public ToeicUserAnswerDTO saveAnswer(Long userId, Long sessionId, AnswerRequest request) {
    ToeicTestSession session =
        sessionRepository
            .findByIdAndUserId(sessionId, userId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        404, "Test session not found or does not belong to the user"));

    if (session.getStatus() == ToeicSessionStatus.COMPLETED) {
      throw new BusinessException(400, "Cannot change answers for a completed session");
    }

    ToeicQuestion question =
        questionRepository
            .findById(request.getQuestionId())
            .orElseThrow(() -> new BusinessException(404, "Question not found"));

    if (question.getTest() == null
        || !question.getTest().getId().equals(session.getTest().getId())) {
      throw new BusinessException(400, "Question does not belong to this test");
    }

    ToeicUserAnswer userAnswer =
        userAnswerRepository
            .findBySessionIdAndQuestionId(sessionId, request.getQuestionId())
            .orElse(null);

    if (userAnswer == null) {
      userAnswer = ToeicUserAnswer.builder().session(session).question(question).build();
    }

    userAnswer.setUserChoice(request.getUserChoice());

    if (request.getIsMarked() != null) {
      userAnswer.setIsMarked(request.getIsMarked());
    }

    // Grade the single answer: 1: Đúng, 0: Sai, 2: Bỏ qua
    if (request.getUserChoice() == null) {
      userAnswer.setStatus(2);
    } else if (request.getUserChoice() == question.getCorrectAns()) {
      userAnswer.setStatus(1);
    } else {
      userAnswer.setStatus(0);
    }

    userAnswer = userAnswerRepository.save(userAnswer);
    return mapToAnswerDTO(userAnswer);
  }

  @Transactional
  public List<ToeicUserAnswerDTO> saveAnswers(
      Long userId, Long sessionId, List<AnswerRequest> requests) {
    if (requests == null || requests.isEmpty()) {
      return Collections.emptyList();
    }

    List<ToeicUserAnswerDTO> results = new ArrayList<>();
    for (AnswerRequest req : requests) {
      results.add(saveAnswer(userId, sessionId, req));
    }
    return results;
  }

  @Transactional
  public ToeicUserAnswerDTO toggleMark(
      Long userId, Long sessionId, Long questionId, Boolean isMarked) {
    ToeicTestSession session =
        sessionRepository
            .findByIdAndUserId(sessionId, userId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        404, "Test session not found or does not belong to the user"));

    ToeicQuestion question =
        questionRepository
            .findById(questionId)
            .orElseThrow(() -> new BusinessException(404, "Question not found"));

    if (question.getTest() == null
        || !question.getTest().getId().equals(session.getTest().getId())) {
      throw new BusinessException(400, "Question does not belong to this test");
    }

    ToeicUserAnswer userAnswer =
        userAnswerRepository.findBySessionIdAndQuestionId(sessionId, questionId).orElse(null);

    if (userAnswer == null) {
      userAnswer =
          ToeicUserAnswer.builder()
              .session(session)
              .question(question)
              .userChoice(null)
              .status(2) // Default to skipped if not answered yet
              .isMarked(isMarked)
              .build();
    } else {
      userAnswer.setIsMarked(isMarked);
    }

    userAnswer = userAnswerRepository.save(userAnswer);
    return mapToAnswerDTO(userAnswer);
  }

  @Transactional
  public ToeicTestSessionDTO submitSession(
      Long userId, Long sessionId, SubmitSessionRequest request) {
    ToeicTestSession session =
        sessionRepository
            .findByIdAndUserId(sessionId, userId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        404, "Test session not found or does not belong to the user"));

    if (session.getStatus() == ToeicSessionStatus.COMPLETED) {
      return mapToSessionDTO(session);
    }

    // Optional: save any answers passed with the submit request
    if (request != null && request.getAnswers() != null && !request.getAnswers().isEmpty()) {
      saveAnswers(userId, sessionId, request.getAnswers());
    }

    session.setCompletedAt(LocalDateTime.now());

    // Calculate time spent
    if (request != null && request.getTimeSpent() != null && request.getTimeSpent() >= 0) {
      session.setTimeSpent(request.getTimeSpent());
    } else {
      long seconds = Duration.between(session.getStartedAt(), session.getCompletedAt()).toSeconds();
      session.setTimeSpent((int) seconds);
    }

    // Retrieve all test questions
    List<ToeicQuestion> allQuestions = session.getTest().getQuestions();

    // Filter questions based on parts if it is a PRACTICE session
    if (session.getMode() == ToeicSessionMode.PRACTICE
        && session.getPartsDone() != null
        && !session.getPartsDone().isEmpty()) {
      List<Integer> parts = session.getPartsDone();
      allQuestions =
          allQuestions.stream()
              .filter(q -> parts.contains(q.getPartNumber()))
              .collect(Collectors.toList());
    }

    // Fetch existing answers from database
    List<ToeicUserAnswer> existingAnswers = userAnswerRepository.findBySessionId(sessionId);
    Map<Long, ToeicUserAnswer> answerMap =
        existingAnswers.stream()
            .collect(Collectors.toMap(ans -> ans.getQuestion().getId(), ans -> ans));

    // Ensure there is a UserAnswer record for every question in the session (non-answered questions
    // are recorded as skipped)
    List<ToeicUserAnswer> finalAnswers = new ArrayList<>();
    for (ToeicQuestion q : allQuestions) {
      ToeicUserAnswer ans = answerMap.get(q.getId());
      if (ans == null) {
        ans =
            ToeicUserAnswer.builder()
                .session(session)
                .question(q)
                .userChoice(null)
                .status(2) // Skipped
                .isMarked(false)
                .build();
        ans = userAnswerRepository.save(ans);
      }
      finalAnswers.add(ans);
    }

    // Count answers
    int correct = 0;
    int incorrect = 0;
    int skipped = 0;

    int listeningTotal = 0;
    int listeningCorrect = 0;
    int readingTotal = 0;
    int readingCorrect = 0;

    for (ToeicUserAnswer ans : finalAnswers) {
      Integer status = ans.getStatus();
      if (status == 1) {
        correct++;
      } else if (status == 0) {
        incorrect++;
      } else {
        skipped++;
      }

      Integer part = ans.getQuestion().getPartNumber();
      boolean isListening = part != null && part >= 1 && part <= 4;
      boolean isReading = part != null && part >= 5 && part <= 7;

      if (isListening) {
        listeningTotal++;
        if (status == 1) {
          listeningCorrect++;
        }
      } else if (isReading) {
        readingTotal++;
        if (status == 1) {
          readingCorrect++;
        }
      }
    }

    session.setCorrectCount(correct);
    session.setIncorrectCount(incorrect);
    session.setSkippedCount(skipped);

    // Calculate Listening & Reading scores scaled to a base of 100 questions
    int scoreL = 0;
    if (listeningTotal > 0) {
      int scaledL = (int) Math.round(((double) listeningCorrect / listeningTotal) * 100);
      scoreL = scaleToToeicScore(scaledL);
    }

    int scoreR = 0;
    if (readingTotal > 0) {
      int scaledR = (int) Math.round(((double) readingCorrect / readingTotal) * 100);
      scoreR = scaleToToeicScore(scaledR);
    }

    session.setScoreL(scoreL);
    session.setScoreR(scoreR);
    session.setTotalScore(scoreL + scoreR);
    session.setStatus(ToeicSessionStatus.COMPLETED);

    session = sessionRepository.save(session);
    return mapToSessionDTO(session);
  }

  @Transactional(readOnly = true)
  public ToeicSessionDetailsResponse getSessionDetails(Long userId, Long sessionId) {
    ToeicTestSession session =
        sessionRepository
            .findByIdAndUserId(sessionId, userId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        404, "Test session not found or does not belong to the user"));

    // Retrieve all test questions
    List<ToeicQuestion> allQuestions = session.getTest().getQuestions();

    // Filter questions if PRACTICE mode
    if (session.getMode() == ToeicSessionMode.PRACTICE
        && session.getPartsDone() != null
        && !session.getPartsDone().isEmpty()) {
      List<Integer> parts = session.getPartsDone();
      allQuestions =
          allQuestions.stream()
              .filter(q -> parts.contains(q.getPartNumber()))
              .collect(Collectors.toList());
    }

    // Fetch user answers from database
    List<ToeicUserAnswer> savedAnswers = userAnswerRepository.findBySessionId(sessionId);
    Map<Long, ToeicUserAnswer> answerMap =
        savedAnswers.stream()
            .collect(Collectors.toMap(ans -> ans.getQuestion().getId(), ans -> ans));

    // Combine questions and user answers
    List<ToeicUserAnswerDTO> dtoList = new ArrayList<>();
    for (ToeicQuestion q : allQuestions) {
      ToeicUserAnswer ans = answerMap.get(q.getId());
      if (ans != null) {
        dtoList.add(mapToAnswerDTO(ans));
      } else {
        // Create virtual skipped answer if session is still in progress
        ToeicUserAnswerDTO virtualAns = new ToeicUserAnswerDTO();
        virtualAns.setSessionId(sessionId);
        virtualAns.setQuestionId(q.getId());
        virtualAns.setPartNumber(q.getPartNumber());
        virtualAns.setUserChoice(null);
        virtualAns.setCorrectChoice(q.getCorrectAns());
        virtualAns.setStatus(2); // Skipped
        virtualAns.setIsMarked(false);
        virtualAns.setQuestionText(q.getQuestion());
        virtualAns.setOptionA(q.getOptionA());
        virtualAns.setOptionB(q.getOptionB());
        virtualAns.setOptionC(q.getOptionC());
        virtualAns.setOptionD(q.getOptionD());
        virtualAns.setExplanation(q.getExplanation());
        virtualAns.setTranscript(q.getTranscript());
        virtualAns.setImageUrl(q.getImageUrl());
        virtualAns.setAudioUrl(q.getAudioUrl());
        dtoList.add(virtualAns);
      }
    }

    // Sort by part number and question ID to keep it structured
    dtoList.sort(
        Comparator.comparing(ToeicUserAnswerDTO::getPartNumber)
            .thenComparing(ToeicUserAnswerDTO::getQuestionId));

    return new ToeicSessionDetailsResponse(mapToSessionDTO(session), dtoList);
  }

  @Transactional(readOnly = true)
  public PagedResponse<ToeicTestSessionDTO> getSessionHistory(Long userId, int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "startedAt"));
    Page<ToeicTestSession> pageResult = sessionRepository.findByUserId(userId, pageable);

    List<ToeicTestSessionDTO> content =
        pageResult.getContent().stream().map(this::mapToSessionDTO).collect(Collectors.toList());

    return new PagedResponse<>(
        content,
        page,
        size,
        pageResult.getTotalElements(),
        pageResult.getTotalPages(),
        pageResult.isFirst(),
        pageResult.isLast(),
        "startedAt",
        "desc");
  }

  private int scaleToToeicScore(int correctCount) {
    if (correctCount <= 0) return 5;
    if (correctCount >= 100) return 495;
    double score = 5.0 + (correctCount * 4.9);
    int roundedScore = (int) (Math.round(score / 5.0) * 5);
    return Math.max(5, Math.min(495, roundedScore));
  }

  private ToeicTestSessionDTO mapToSessionDTO(ToeicTestSession session) {
    ToeicTestSessionDTO dto = new ToeicTestSessionDTO();
    dto.setId(session.getId());
    dto.setUserId(session.getUser().getId());
    dto.setTestId(session.getTest().getId());
    dto.setTestTitle(session.getTest().getTitle());
    dto.setMode(session.getMode());
    dto.setStatus(session.getStatus());
    dto.setScoreL(session.getScoreL());
    dto.setScoreR(session.getScoreR());
    dto.setTotalScore(session.getTotalScore());
    dto.setCorrectCount(session.getCorrectCount());
    dto.setIncorrectCount(session.getIncorrectCount());
    dto.setSkippedCount(session.getSkippedCount());
    dto.setTimeSpent(session.getTimeSpent());
    dto.setPartsDone(session.getPartsDone());
    dto.setStartedAt(session.getStartedAt());
    dto.setCompletedAt(session.getCompletedAt());
    return dto;
  }

  private ToeicUserAnswerDTO mapToAnswerDTO(ToeicUserAnswer answer) {
    ToeicUserAnswerDTO dto = new ToeicUserAnswerDTO();
    dto.setId(answer.getId());
    dto.setSessionId(answer.getSession().getId());
    dto.setQuestionId(answer.getQuestion().getId());
    dto.setPartNumber(answer.getQuestion().getPartNumber());
    dto.setUserChoice(answer.getUserChoice());
    dto.setCorrectChoice(answer.getQuestion().getCorrectAns());
    dto.setStatus(answer.getStatus());
    dto.setIsMarked(answer.getIsMarked());
    dto.setQuestionText(answer.getQuestion().getQuestion());
    dto.setOptionA(answer.getQuestion().getOptionA());
    dto.setOptionB(answer.getQuestion().getOptionB());
    dto.setOptionC(answer.getQuestion().getOptionC());
    dto.setOptionD(answer.getQuestion().getOptionD());
    dto.setExplanation(answer.getQuestion().getExplanation());
    dto.setTranscript(answer.getQuestion().getTranscript());
    dto.setImageUrl(answer.getQuestion().getImageUrl());
    dto.setAudioUrl(answer.getQuestion().getAudioUrl());
    return dto;
  }
}
