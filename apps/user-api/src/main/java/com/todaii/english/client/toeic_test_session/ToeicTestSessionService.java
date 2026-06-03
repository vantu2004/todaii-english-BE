package com.todaii.english.client.toeic_test_session;

import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.*;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.todaii.english.client.toeic_question.QuestionRepository;
import com.todaii.english.client.toeic_test.TestRepository;
import com.todaii.english.client.user.UserRepository;
import com.todaii.english.core.entity.toeic.ToeicQuestion;
import com.todaii.english.core.entity.toeic.ToeicTest;
import com.todaii.english.core.entity.toeic.ToeicTestSession;
import com.todaii.english.core.entity.toeic.ToeicUserAnswer;
import com.todaii.english.core.entity.user.User;
import com.todaii.english.shared.enums.ToeicSessionMode;
import com.todaii.english.shared.enums.ToeicSessionStatus;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.client.AnswerRequest;
import com.todaii.english.shared.request.client.StartSessionRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ToeicTestSessionService {
  private final ToeicTestSessionRepository sessionRepository;
  private final ToeicUserAnswerRepository userAnswerRepository;
  private final UserRepository userRepository;
  private final TestRepository testRepository;
  private final QuestionRepository questionRepository;

  public ToeicTestSession getToeicTestSession(Long userId, Long sessionId) {
    return sessionRepository
        .findByIdAndUserId(sessionId, userId)
        .orElseThrow(
            () ->
                new BusinessException(
                    404, "Test session not found or does not belong to the user"));
  }

  public List<ToeicTestSession> getSessionHistory(Long userId) {
    return sessionRepository.findByUserId(userId);
  }

  public ToeicTestSession startSession(Long userId, StartSessionRequest request) {
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
            .status(ToeicSessionStatus.IN_PROGRESS)
            .user(user)
            .test(test)
            .build();

    if (!request.getMode().equals(ToeicSessionMode.FULL_TEST)) {
      if (request.getTimeSpent() == null) {
        throw new BusinessException(400, "Time spent is required");
      }

      if (!StringUtils.hasText(request.getPartsDone())) {
        throw new BusinessException(400, "Part must not be blank");
      }

      session.setMode(request.getMode());
      session.setTimeSpent(request.getTimeSpent());
      session.setPartsDone(request.getPartsDone());
    }

    return sessionRepository.save(session);
  }

  public ToeicUserAnswer saveAnswer(ToeicTestSession toeicTestSession, AnswerRequest request) {
    if (toeicTestSession.getStatus() == ToeicSessionStatus.COMPLETED) {
      throw new BusinessException(400, "Cannot change answers for a completed session");
    }

    ToeicQuestion question =
        questionRepository
            .findById(request.getQuestionId())
            .orElseThrow(() -> new BusinessException(404, "Question not found"));

    if (question.getTest() == null
        || !question.getTest().getId().equals(toeicTestSession.getTest().getId())) {
      throw new BusinessException(400, "Question does not belong to this test");
    }

    ToeicUserAnswer userAnswer =
        userAnswerRepository
            .findBySessionIdAndQuestionId(toeicTestSession.getId(), request.getQuestionId())
            .orElse(new ToeicUserAnswer());

    userAnswer.setUserChoice(request.getUserChoice());

    // user để null nghĩa là skip = 2
    Integer status =
        request.getUserChoice() != null
            ? (request.getUserChoice().equals(question.getCorrectAns()) ? 1 : 0)
            : 2;
    userAnswer.setStatus(status);

    userAnswer.setIsMarked(request.getIsMarked());
    userAnswer.setSession(toeicTestSession);
    userAnswer.setQuestion(question);

    return userAnswer;
  }

  public List<ToeicUserAnswer> saveAnswers(
      Long userId, Long sessionId, List<AnswerRequest> requests) {
    if (requests == null || requests.isEmpty()) {
      throw new BusinessException(400, "Answer request is required.");
    }

    ToeicTestSession session = getToeicTestSession(userId, sessionId);
    session.setStoppedAt(LocalDateTime.now());

    List<ToeicUserAnswer> answers =
        requests.stream().map(req -> saveAnswer(session, req)).toList();

    return userAnswerRepository.saveAll(answers);
  }

  public ToeicTestSession submitSession(Long userId, Long sessionId, List<AnswerRequest> requests) {
    ToeicTestSession session = getToeicTestSession(userId, sessionId);

    if (session.getStatus() == ToeicSessionStatus.COMPLETED) {
      throw new BusinessException(409, "The test was completed and submitted earlier.");
    }

    if (requests == null || requests.isEmpty()) {
      throw new BusinessException(400, "Answer request is required.");
    }

    List<ToeicUserAnswer> answers = saveAnswers(userId, sessionId, requests);

    Integer correctCount = 0;
    Integer incorrectCount = 0;
    Integer skippedCount = 0;
    int listeningCorrect = 0;
    int readingCorrect = 0;

    for (ToeicUserAnswer answer : answers) {
      Integer status = answer.getStatus();
      Integer part = answer.getQuestion().getPartNumber();

      if (status == 2) {
        skippedCount++;
      }

      if (status == 1) {
        correctCount++;

        if (part >= 1 && part <= 4) {
          listeningCorrect++;
        } else if (part >= 5 && part <= 7) {
          readingCorrect++;
        }
      } else if (status == 0) {
        incorrectCount++;
      }
    }

    // tính theo điểm toeic nên x5
    Integer scoreL = listeningCorrect * 5;
    Integer scoreR = readingCorrect * 5;

    session.setScoreL(scoreL);
    session.setScoreR(scoreR);
    session.setTotalScore(scoreL + scoreR);

    session.setCorrectCount(correctCount);
    session.setIncorrectCount(incorrectCount);
    session.setSkippedCount(skippedCount);

    session.setCompletedAt(LocalDateTime.now());
    session.setStatus(ToeicSessionStatus.COMPLETED);

    return sessionRepository.save(session);
  }
}
