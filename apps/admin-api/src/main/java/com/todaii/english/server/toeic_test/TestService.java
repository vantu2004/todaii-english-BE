package com.todaii.english.server.toeic_test;

import com.todaii.english.core.entity.ToeicCollection;
import com.todaii.english.core.entity.ToeicTest;
import com.todaii.english.server.toeic_collection.CollectionRepository;
import com.todaii.english.shared.dto.ToeicTestDTO;
import com.todaii.english.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {
    private final ModelMapper modelMapper;
    private final TestRepository testRepository;
    private final CollectionRepository collectionRepository;

    public Page<ToeicTestDTO> getPaged(Long collectionId, Pageable pageable) {

        Page<ToeicTest> page;

        if (collectionId != null) {
            page = testRepository.findByCollectionId(collectionId, pageable);
        } else {
            page = testRepository.findAll(pageable);
        }

        return page.map(this::toDTO);
    }

    public ToeicTestDTO getById(Long id) {
        ToeicTest test = testRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Test not found"));

        return toDTO(test);
    }

    public ToeicTestDTO create(Long adminId, ToeicTestDTO dto){

        if (dto.getCollectionId() == null) {
            throw new BusinessException(400, "collectionId is required");
        }

        ToeicCollection collection = collectionRepository.findById(dto.getCollectionId())
                .orElseThrow(() -> new BusinessException(404, "Collection not found"));

        ToeicTest test = ToeicTest.builder()
                .collection(collection)
                .title(dto.getTitle())
                .testType(dto.getTestType())
                .duration(dto.getDuration())
                .audioUrl(dto.getAudioUrl())
                .thumbnail(dto.getThumbnail())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .creatorId(adminId)
                .build();

        return toDTO(testRepository.save(test));
    }

    public ToeicTestDTO update(Long id, ToeicTestDTO dto) {

        ToeicTest test = testRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Test not found"));

        if (dto.getTitle() != null) test.setTitle(dto.getTitle());
        if (dto.getTestType() != null) test.setTestType(dto.getTestType());
        if (dto.getDuration() != null) test.setDuration(dto.getDuration());
        if (dto.getAudioUrl() != null) test.setAudioUrl(dto.getAudioUrl());
        if (dto.getThumbnail() != null) test.setThumbnail(dto.getThumbnail());
        if (dto.getDescription() != null) test.setDescription(dto.getDescription());
        if (dto.getStatus() != null) test.setStatus(dto.getStatus());

        if (dto.getCollectionId() != null) {
            ToeicCollection collection = collectionRepository.findById(dto.getCollectionId())
                    .orElseThrow(() -> new BusinessException(404, "Collection not found"));
            test.setCollection(collection);
        }

        ToeicTest saved = testRepository.save(test);

        return toDTO(saved);
    }

    public void delete(Long id) {
        ToeicTest test = testRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Test not found"));

        testRepository.delete(test);
    }

    private ToeicTestDTO toDTO(ToeicTest entity) {

        ToeicTestDTO dto = modelMapper.map(entity, ToeicTestDTO.class);

        if (entity.getCollection() != null) {
            dto.setCollectionId(entity.getCollection().getId());
            dto.setCollectionName(entity.getCollection().getName());
        }

        return dto;
    }

}
