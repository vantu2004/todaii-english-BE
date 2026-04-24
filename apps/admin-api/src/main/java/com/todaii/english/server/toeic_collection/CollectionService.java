package com.todaii.english.server.toeic_collection;

import com.todaii.english.core.entity.ToeicCollection;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.ToeicCollectionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionService {
    private final CollectionRepository collectionRepository;

    public List<ToeicCollection> findAll() {
        return collectionRepository.findAll();
    }

    public Page<ToeicCollection> findAllPaged(int page, int size, String sortBy, String direction, String keyword) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        return collectionRepository.search(keyword, pageable);
    }

    public ToeicCollection findById(Long collectionId){
        return collectionRepository.findById(collectionId).orElseThrow(() -> new BusinessException(404, "Collection not found"));
    }

    public ToeicCollection create(ToeicCollectionRequest request) {
        ToeicCollection collection = ToeicCollection.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return collectionRepository.save(collection);
    }

    public ToeicCollection update(Long id, ToeicCollectionRequest request) {
        ToeicCollection collection = findById(id);

        collection.setName(request.getName());
        collection.setDescription(request.getDescription());

        return collectionRepository.save(collection);
    }

    public void deleteById(Long id) {
        collectionRepository.deleteById(id);
    }
}
