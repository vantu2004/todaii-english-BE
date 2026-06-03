package com.todaii.english.client.toeic_test_session;

public class ToeicScoreConverter {

  // Bảng quy đổi điểm Listening chuẩn theo ảnh (từ 0 đến 100 câu đúng)
  private static final int[] LISTENING_SCORES = {
    5, 15, 20, 25, 30, 35, 40, 45, 50, 55, // 0 - 9
    60, 65, 70, 75, 80, 85, 90, 95, 100, 105, // 10 - 19
    110, 115, 120, 125, 130, 135, 140, 145, 150, 155, // 20 - 29
    160, 165, 170, 175, 180, 185, 190, 195, 200, 205, // 30 - 39
    210, 215, 220, 225, 230, 235, 240, 245, 250, 255, // 40 - 49
    260, 265, 270, 275, 280, 285, 290, 295, 300, 305, // 50 - 59
    310, 315, 320, 325, 330, 335, 340, 345, 350, 355, // 60 - 69
    360, 365, 370, 375, 380, 385, 395, 400, 405, 410, // 70 - 79 (Lưu ý mốc 75->76 nhảy 10 điểm)
    415, 420, 425, 430, 435, 440, 445, 450, 455, 460, // 80 - 89
    465, 470, 475, 480, 485, 490, 495, 495, 495, 495, // 90 - 99
    495 // 100
  };

  // Bảng quy đổi điểm Reading chuẩn theo ảnh (từ 0 đến 100 câu đúng)
  private static final int[] READING_SCORES = {
    5, 5, 5, 10, 15, 20, 25, 30, 35, 40, // 0 - 9
    45, 50, 55, 60, 65, 70, 75, 80, 85, 90, // 10 - 19
    95, 100, 105, 110, 115, 120, 125, 130, 135, 140, // 20 - 29
    145, 150, 155, 160, 165, 170, 175, 180, 185, 190, // 30 - 39
    195, 200, 205, 210, 215, 220, 225, 230, 235, 240, // 40 - 49
    245, 250, 255, 260, 265, 270, 275, 280, 285, 290, // 50 - 59
    295, 300, 305, 310, 315, 320, 325, 330, 335, 340, // 60 - 69
    345, 350, 355, 360, 365, 370, 375, 380, 385, 390, // 70 - 79
    395, 400, 405, 410, 415, 420, 425, 430, 435, 440, // 80 - 89
    445, 450, 455, 460, 465, 470, 475, 480, 485, 490, // 90 - 99
    495 // 100
  };

  /** Quy đổi số câu đúng phần Nghe sang điểm số TOEIC chuẩn (5 - 495) */
  public static Integer convertListeningScore(int correctAnswers) {
    // Phòng ngừa lỗi truyền số âm hoặc vượt quá 100 câu hỏi
    int clampedAnswers = Math.max(0, Math.min(correctAnswers, 100));
    return LISTENING_SCORES[clampedAnswers];
  }

  /** Quy đổi số câu đúng phần Đọc sang điểm số TOEIC chuẩn (5 - 495) */
  public static Integer convertReadingScore(int correctAnswers) {
    // Phòng ngừa lỗi truyền số âm hoặc vượt quá 100 câu hỏi
    int clampedAnswers = Math.max(0, Math.min(correctAnswers, 100));
    return READING_SCORES[clampedAnswers];
  }
}
