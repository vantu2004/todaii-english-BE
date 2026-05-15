package com.todaii.english.infra.config;

import com.todaii.english.core.entity.toeic.ToeicQuestion;
import com.todaii.english.shared.request.server.toeic.Part12Request;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    // Matching STRICT để tránh map nhầm field tương tự (phân biệt cả hoa-thường)
    mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    // Mapping field imageUrl và audioUrl cho các nested classes trong Part12Request
    mapper.typeMap(Part12Request.class, ToeicQuestion.class)
            .addMappings(
                    m -> {
                      m.map(src -> src.getImageRequest().getImageUrl(), ToeicQuestion::setImageUrl);
                      m.map(src -> src.getAudioRequest().getAudioUrl(), ToeicQuestion::setAudioUrl);
                    });
    return mapper;
  }
}
