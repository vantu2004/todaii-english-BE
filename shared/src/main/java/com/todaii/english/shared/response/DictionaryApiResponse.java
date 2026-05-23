package com.todaii.english.shared.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DictionaryApiResponse {
  private String word;
  private String phonetic;
  private List<Phonetic> phonetics;
  private List<Meaning> meanings;

  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class Phonetic {
    private String text;
    private String audio;
  }

  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class Meaning {
    private String partOfSpeech;
    private List<Definition> definitions;
  }

  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class Definition {
    private String definition;
    private String example;
    private List<String> synonyms;
    private List<String> antonyms;
  }
}
