package com.todaii.english.server.toeic_tag;

import com.todaii.english.core.entity.ToeicTag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/toeic/tag")
@RequiredArgsConstructor
public class TagApiController {

    private final TagService tagService;

    @GetMapping
    public Page<ToeicTag> getAllPaged(Pageable pageable) {
        return tagService.getAllPaged(pageable);
    }

    @GetMapping("/{id}")
    public ToeicTag getById(@PathVariable Long id) {
        return tagService.findById(id);
    }

    @PostMapping
    public ToeicTag create(@RequestParam String name, @RequestParam String tagType) {
        return tagService.create(name, tagType);
    }

    @PutMapping("/{id}")
    public ToeicTag update(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String tagType
    ) {
        return tagService.update(id, name, tagType);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        tagService.delete(id);
    }
}
