package com.todaii.english.client.toeic_collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.toeic.ToeicCollection;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollectionService {
  private final CollectionRepository collectionRepository;

  public Page<ToeicCollection> findAllPaged(
      int page, int size, String sortBy, String direction, String keyword) {
    Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    Pageable pageable = PageRequest.of(page - 1, size, sort);

    return collectionRepository.search(keyword, pageable);
  }

  public ToeicCollection getById(Long id) {
    return collectionRepository
        .findById(id)
        .orElseThrow(() -> new BusinessException(404, "Collection not found"));
  }
}
