package com.todaii.english.server.toeic_collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.ToeicCollection;
import com.todaii.english.server.AdminUtils;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.toeic.ToeicCollectionRequest;

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

  public ToeicCollection findById(Long collectionId) {
    return collectionRepository
        .findById(collectionId)
        .orElseThrow(() -> new BusinessException(404, "Collection not found"));
  }

  public ToeicCollection create(ToeicCollectionRequest request) {
    String alias = AdminUtils.toAlias(request.getName());
    if (collectionRepository.existsByAlias(alias)) {
      throw new BusinessException(409, "Alias already exists: " + alias);
    }

    ToeicCollection collection =
        ToeicCollection.builder()
            .name(request.getName())
            .alias(alias)
            .description(request.getDescription())
            .build();

    return collectionRepository.save(collection);
  }

  public ToeicCollection update(Long id, ToeicCollectionRequest request) {
    ToeicCollection collection = findById(id);

    String alias = AdminUtils.toAlias(request.getName());

    // chỉ check khi alias thực sự thay đổi
    if (!alias.equals(collection.getAlias())) {
      boolean aliasExists = collectionRepository.existsByAlias(alias);

      if (aliasExists) {
        throw new BusinessException(409, "Alias already exists: " + alias);
      }
    }

    collection.setName(request.getName().trim());
    collection.setAlias(alias);
    collection.setDescription(request.getDescription());

    return collectionRepository.save(collection);
  }

  public void softDelete(Long id) {
    ToeicCollection toeicCollection = findById(id);

    toeicCollection.setName("deleted-" + toeicCollection.getName());
    toeicCollection.setAlias("deleted-" + toeicCollection.getAlias());
    toeicCollection.setIsDeleted(true);

    collectionRepository.save(toeicCollection);
  }

  public void toggleEnabled(Long id) {
    ToeicCollection toeicCollection = findById(id);
    toeicCollection.setEnabled(!toeicCollection.getEnabled());

    collectionRepository.save(toeicCollection);
  }
}
