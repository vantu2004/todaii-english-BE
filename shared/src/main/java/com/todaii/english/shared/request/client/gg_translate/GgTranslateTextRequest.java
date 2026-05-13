package com.todaii.english.shared.request.client.gg_translate;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GgTranslateTextRequest extends GgTranslateBaseRequest {
  @NotNull(message = "Texts must not be null")
  @NotEmpty(message = "Texts must not be empty")
  @Size(max = 20, message = "Maximum 20 texts allowed")
  private List<
          @NotBlank(message = "Text must not be blank")
          @Size(max = 1024, message = "Each text must be less than 1024 characters") String>
      texts;
}
