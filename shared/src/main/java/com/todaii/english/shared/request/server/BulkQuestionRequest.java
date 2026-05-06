package com.todaii.english.shared.request.server;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import com.todaii.english.shared.dto.ToeicQuestionDTO;

@Data
public class BulkQuestionRequest {

    @Valid
    @NotEmpty
    private List<ToeicQuestionDTO> questions;
}
