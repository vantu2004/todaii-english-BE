package com.todaii.english.server.toeic_collection;

import com.todaii.english.core.entity.ToeicCollection;
import com.todaii.english.shared.request.server.toeic.CollectionRequest;
import com.todaii.english.shared.response.PagedResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @GetMapping
    public ResponseEntity<PagedResponse<ToeicCollection>> getAllPaged(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword) {

        Page<ToeicCollection> collections = collectionService.findAllPaged(page, size, sortBy, direction, keyword);

        PagedResponse<ToeicCollection> response = new PagedResponse<>(collections.getContent(), page, size,
                collections.getTotalElements(), collections.getTotalPages(), collections.isFirst(), collections.isLast(), sortBy,
                direction);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToeicCollection> getById(@PathVariable Long id) {
        return ResponseEntity.ok(collectionService.findById(id));
    }

    @GetMapping("/all")
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
    public ResponseEntity<ToeicCollection> updateCollection(@PathVariable Long id, @Valid @RequestBody CollectionRequest request){
        return ResponseEntity.ok(collectionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        collectionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
