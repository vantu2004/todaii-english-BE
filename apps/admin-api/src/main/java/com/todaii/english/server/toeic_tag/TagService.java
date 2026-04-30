package com.todaii.english.server.toeic_tag;


import com.todaii.english.core.entity.ToeicTag;
import com.todaii.english.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    public Page<ToeicTag> getAllPaged(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    public ToeicTag findById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Tag not found"));
    }

    public ToeicTag create(String name, String tagType) {
        ToeicTag tag = ToeicTag.builder()
                        .name(name)
                        .tagType(tagType)
                        .build();
        return tagRepository.save(tag);
    }

    public ToeicTag update(Long id, String name, String tagType) {
        ToeicTag tag = findById(id);
        tag.setName(name);
        tag.setTagType(tagType);
        return tagRepository.save(tag);
    }

    public void delete(Long id) {
        tagRepository.delete(findById(id));
    }


}
