package com.todaii.english.client.toeic_test_session;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
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
import com.todaii.english.shared.dto.toeic.ToeicTestSessionDTO;
import com.todaii.english.shared.dto.toeic.ToeicUserAnswerDTO;
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
  private final ModelMapper modelMapper;

  public ToeicTestSession getToeicTestSession(Long userId, Long sessionId) {
    return sessionRepository
        .findByIdAndUserId(sessionId, userId)
        .orElseThrow(
            () ->
                new BusinessException(
                    404, "Test session not found or does not belong to the user"));
  }

  public ToeicTestSessionDTO getSessionDetailsDto(Long userId, Long sessionId) {
    ToeicTestSession session = getToeicTestSession(userId, sessionId);
    List<ToeicUserAnswer> answers = userAnswerRepository.findBySessionId(sessionId);

    return mapToSessionDto(session, answers);
  }

  public List<ToeicTestSessionDTO> getSessionHistory(Long userId) {
    List<ToeicTestSession> sessions = sessionRepository.findByUserId(userId);

    return sessions.stream()
        .map(session -> mapToSessionDto(session, Collections.emptyList()))
        .toList();
  }

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
            .status(ToeicSessionStatus.IN_PROGRESS)
            .user(user)
            .test(test)
            .build();

    if (request.getMode() != null && !request.getMode().equals(ToeicSessionMode.FULL_TEST)) {
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

    ToeicTestSession savedSession = sessionRepository.save(session);
    return mapToSessionDto(savedSession, Collections.emptyList());
  }

  public List<ToeicUserAnswerDTO> saveAnswers(
      Long userId, Long sessionId, List<AnswerRequest> requests) {
    if (requests == null || requests.isEmpty()) {
      throw new BusinessException(400, "Answer request is required.");
    }

    ToeicTestSession session = getToeicTestSession(userId, sessionId);
    if (session.getStatus() == ToeicSessionStatus.COMPLETED) {
      throw new BusinessException(400, "Cannot change answers for a completed session");
    }

    // BATCH OPTIMIZATION: Lấy tất cả câu hỏi liên quan trong 1 Query duy nhất
    List<Long> questionIds = requests.stream().map(AnswerRequest::getQuestionId).toList();
    Map<Long, ToeicQuestion> questionMap =
        questionRepository.findAllById(questionIds).stream()
            .collect(Collectors.toMap(ToeicQuestion::getId, q -> q));

    // BATCH OPTIMIZATION: Lấy toàn bộ câu trả lời cũ của session này để kiểm tra xem đã tồn tại
    // chưa
    Map<Long, ToeicUserAnswer> existingAnswersMap =
        userAnswerRepository.findBySessionId(sessionId).stream()
            .collect(Collectors.toMap(ans -> ans.getQuestion().getId(), ans -> ans));

    List<ToeicUserAnswer> answersToSave = new ArrayList<>();

    for (AnswerRequest req : requests) {
      ToeicQuestion question = questionMap.get(req.getQuestionId());
      if (question == null
          || question.getTest() == null
          || !question.getTest().getId().equals(session.getTest().getId())) {
        throw new BusinessException(400, "Question invalid or does not belong to this test");
      }

      // Nếu đã tồn tại thì cập nhật, chưa thì tạo mới bản ghi answers
      ToeicUserAnswer userAnswer =
          existingAnswersMap.getOrDefault(req.getQuestionId(), new ToeicUserAnswer());
      userAnswer.setSession(session);
      userAnswer.setQuestion(question);
      userAnswer.setUserChoice(req.getUserChoice());
      userAnswer.setIsMarked(req.getIsMarked() != null && req.getIsMarked());

      // 1: Đúng, 0: Sai, 2: Bỏ qua (UserChoice == null)
      Integer status =
          (req.getUserChoice() != null)
              ? (req.getUserChoice().equals(question.getCorrectAns()) ? 1 : 0)
              : 2;
      userAnswer.setStatus(status);

      answersToSave.add(userAnswer);
    }

    session.setStoppedAt(LocalDateTime.now()); // Đóng vai trò trường cập nhật cuối (updatedAt)
    sessionRepository.save(session);

    List<ToeicUserAnswer> savedAnswers = userAnswerRepository.saveAll(answersToSave);
    return savedAnswers.stream().map(ans -> mapToAnswerDto(ans, session.getStatus())).toList();
  }

  public ToeicTestSessionDTO submitSession(
      Long userId, Long sessionId, List<AnswerRequest> requests) {
    ToeicTestSession session = getToeicTestSession(userId, sessionId);

    if (session.getStatus() == ToeicSessionStatus.COMPLETED) {
      throw new BusinessException(409, "The test was completed and submitted earlier.");
    }

    // Lưu lượt đáp án cuối cùng nếu Client có gửi lên kèm theo lệnh Submit
    if (requests != null && !requests.isEmpty()) {
      saveAnswers(userId, sessionId, requests);
    }

    // FIX LOGIC BUG: Lấy TOÀN BỘ câu trả lời trong DB của session này lên tính điểm, không chỉ lấy
    // ở request cuối
    List<ToeicUserAnswer> allAnswers = userAnswerRepository.findBySessionId(sessionId);

    int correctCount = 0;
    int incorrectCount = 0;
    int skippedCount = 0;
    int listeningCorrect = 0;
    int readingCorrect = 0;

    for (ToeicUserAnswer answer : allAnswers) {
      Integer status = answer.getStatus();
      Integer part = answer.getQuestion().getPartNumber();

      if (status == 2) {
        skippedCount++;
      } else if (status == 1) {
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

    // FIX LOGIC BUG: Thay đổi công thức nhân tuyến tính *5 lỗi thời bằng bảng chuyển đổi chuẩn phi
    // tuyến
    Integer scoreL = ToeicScoreConverter.convertListeningScore(listeningCorrect);
    Integer scoreR = ToeicScoreConverter.convertReadingScore(readingCorrect);

    session.setScoreL(scoreL);
    session.setScoreR(scoreR);
    session.setTotalScore(scoreL + scoreR);
    session.setCorrectCount(correctCount);
    session.setIncorrectCount(incorrectCount);
    session.setSkippedCount(skippedCount);
    session.setCompletedAt(LocalDateTime.now());
    session.setStatus(ToeicSessionStatus.COMPLETED);

    ToeicTestSession completedSession = sessionRepository.save(session);
    return mapToSessionDto(completedSession, allAnswers);
  }

  // --- MAPPING HELPERS (Ngăn chặn lộ đáp án chuẩn khi đang thi) ---
  private ToeicTestSessionDTO mapToSessionDto(
      ToeicTestSession session, List<ToeicUserAnswer> answers) {
    List<ToeicUserAnswerDTO> answerDtos =
        answers.stream().map(ans -> mapToAnswerDto(ans, session.getStatus())).toList();

    ToeicTestSessionDTO toeicTestSessionDTO = modelMapper.map(session, ToeicTestSessionDTO.class);
    toeicTestSessionDTO.setTestId(session.getTest() != null ? session.getTest().getId() : null);
    toeicTestSessionDTO.setUserAnswers(answerDtos);

    return toeicTestSessionDTO;
  }

  private ToeicUserAnswerDTO mapToAnswerDto(ToeicUserAnswer ans, ToeicSessionStatus sessionStatus) {
    ToeicUserAnswerDTO toeicUserAnswerDTO =
        ToeicUserAnswerDTO.builder()
            .id(ans.getId())
            .questionId(ans.getQuestion().getId())
            .userChoice(ans.getUserChoice())
            .isMarked(ans.getIsMarked())
            .build();

    // bao giờ submit mới cho biết trạng thái câu
    if (sessionStatus == ToeicSessionStatus.COMPLETED) {
      toeicUserAnswerDTO.setStatus(ans.getStatus());
    } else {
      toeicUserAnswerDTO.setStatus(null);
    }

    return toeicUserAnswerDTO;
  }
}
