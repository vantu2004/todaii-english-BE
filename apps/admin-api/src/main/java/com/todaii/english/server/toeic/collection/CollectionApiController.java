package com.todaii.english.server.toeic.collection;

import com.todaii.english.core.entity.ToeicCollection;
import com.todaii.english.shared.request.server.toeic.CollectionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/toeic/collection")
public class CollectionApiController {
    private final CollectionService collectionService;

    @GetMapping("/{id}")
    public ResponseEntity<ToeicCollection> getById(@PathVariable Long id) {
        return ResponseEntity.ok(collectionService.findById(id));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Hello");
    }

    @GetMapping
    public ResponseEntity<List<ToeicCollection>> getAllCollections() {
        List<ToeicCollection> collections = collectionService.findAll();
        if (collections.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(collections);
    }

    @PostMapping
    public ResponseEntity<ToeicCollection> createCollection(@Valid @RequestBody CollectionRequest request) {
        return ResponseEntity.status(201).body(collectionService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ToeicCollection> updateCollection(@PathVariable Long id ,@Valid @RequestBody CollectionRequest request){
        return ResponseEntity.ok(collectionService.update(id, request));
    }
}
