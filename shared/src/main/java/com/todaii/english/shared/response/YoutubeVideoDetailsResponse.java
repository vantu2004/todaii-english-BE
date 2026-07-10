package com.todaii.english.shared.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutubeVideoDetailsResponse {
  private List<VideoItem> items;

  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class VideoItem {
    private String id;
    private ContentDetails contentDetails;
  }

  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ContentDetails {
    private String duration;
  }
}
