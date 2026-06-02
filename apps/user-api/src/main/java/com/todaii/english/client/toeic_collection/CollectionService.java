package com.todaii.english.client.toeic_collection;

import java.util.List;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.toeic.ToeicCollection;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollectionService {
  private final CollectionRepository collectionRepository;

  public List<ToeicCollection> getAllCollections() {
    return collectionRepository.findAllByOrderByNameAsc();
  }
}
