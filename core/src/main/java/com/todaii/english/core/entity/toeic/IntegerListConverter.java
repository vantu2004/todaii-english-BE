package com.todaii.english.core.entity.toeic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter
public class IntegerListConverter implements AttributeConverter<List<Integer>, String> {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(List<Integer> attribute) {
    if (attribute == null) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Error converting list to JSON string.", e);
    }
  }

  @Override
  public List<Integer> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return new ArrayList<>();
    }
    try {
      return objectMapper.readValue(dbData, new TypeReference<List<Integer>>() {});
    } catch (IOException e) {
      throw new IllegalArgumentException("Error converting JSON string to list.", e);
    }
  }
}
