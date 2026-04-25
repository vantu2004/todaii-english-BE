package com.todaii.english.shared.dto;

import com.todaii.english.shared.enums.Answer;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicQuestionDTO {

    private Long testId;
    private Long groupId;

    @NotNull
    private Integer partNumber;

    @NotNull
    private Integer questionNo;

    private String question;
    private String imageUrl;
    private String audioUrl;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    @NotNull
    private Answer correctAns;

    private String explanation;
    private String translation;
}