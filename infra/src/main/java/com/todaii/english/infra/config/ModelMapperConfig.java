package com.todaii.english.infra.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.todaii.english.core.entity.toeic.ToeicPassage;
import com.todaii.english.core.entity.toeic.ToeicQuestion;
import com.todaii.english.shared.dto.ToeicQuestionDTO;
import com.todaii.english.shared.request.server.toeic.Part12Request;
import com.todaii.english.shared.request.server.toeic.ToeicPassageRequest;

@Configuration
public class ModelMapperConfig {
  @Bean
  public ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    // Matching STRICT để tránh map nhầm field tương tự (phân biệt cả hoa-thường)
    mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    // Mapping field imageUrl và audioUrl cho các nested classes trong Part12Request,
    // ToeicPassageRequest
    mapper
        .typeMap(Part12Request.class, ToeicQuestion.class)
        .addMappings(
            m -> {
              m.map(src -> src.getImageRequest().getImageUrl(), ToeicQuestion::setImageUrl);
              m.map(src -> src.getAudioRequest().getAudioUrl(), ToeicQuestion::setAudioUrl);
            });

    mapper
        .typeMap(ToeicPassageRequest.class, ToeicPassage.class)
        .addMappings(
            m -> {
              m.map(src -> src.getImageRequest().getImageUrl(), ToeicPassage::setImageUrl);
              m.map(src -> src.getAudioRequest().getAudioUrl(), ToeicPassage::setAudioUrl);
            });

    // Map từ passage trong ToeicQuestion cho passageId trong ToeicQuestionDTO
    mapper
        .typeMap(ToeicQuestion.class, ToeicQuestionDTO.class)
        .addMappings(m -> m.map(src -> src.getPassage().getId(), ToeicQuestionDTO::setPassageId));
    return mapper;
  }
}
