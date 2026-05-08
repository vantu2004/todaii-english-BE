package com.todaii.english.shared.request.server.toeic;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import com.todaii.english.shared.dto.ToeicQuestionDTO;

import lombok.Data;

@Data
public class BulkQuestionRequest {

  @Valid @NotEmpty private List<ToeicQuestionDTO> questions;
}
