package com.todaii.english.server.toeic.collection;

import com.todaii.english.core.entity.ToeicCollection;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.toeic.CollectionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionService {
    private final CollectionRepository collectionRepository;
    public List<ToeicCollection> findAll() {
        return collectionRepository.findAll();
    }

    public ToeicCollection findById(Long collectionId){
        return collectionRepository.findById(collectionId).orElseThrow(() -> new BusinessException(404, "Collection not found"));
    }

    public ToeicCollection create(CollectionRequest request) {
        ToeicCollection collection = ToeicCollection.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return collectionRepository.save(collection);
    }

    public ToeicCollection update(Long id, CollectionRequest request) {
        ToeicCollection collection = findById(id);

        collection.setName(request.getName());
        collection.setDescription(request.getDescription());

        return collectionRepository.save(collection);
    }
}
