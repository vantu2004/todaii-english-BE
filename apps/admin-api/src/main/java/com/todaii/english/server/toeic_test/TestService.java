package com.todaii.english.server.toeic_test;

import com.todaii.english.shared.response.ToeicTestResponse;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.ToeicCollection;
import com.todaii.english.core.entity.ToeicTest;
import com.todaii.english.server.admin.AdminRepository;
import com.todaii.english.server.toeic_collection.CollectionRepository;
import com.todaii.english.shared.request.server.ToeicTestRequest;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestService {
    private final ModelMapper modelMapper;
    private final TestRepository testRepository;
    private final CollectionRepository collectionRepository;
    private final AdminRepository adminRepository;

    public Page<ToeicTestResponse> getAllPaged(int page, int size, String sortBy, String direction, String keyword) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<ToeicTest> toeicTestPage = testRepository.getAllPaged(keyword, pageable);

        return toeicTestPage.map(toeicTest -> modelMapper.map(toeicTest, ToeicTestResponse.class));
    }

    public ToeicTestResponse getById(Long id) {
        ToeicTest toeicTest = testRepository
                .findById(id)
                .orElseThrow(() -> new BusinessException(404, "Test not found"));

        return modelMapper.map(toeicTest, ToeicTestResponse.class);
    }

    public ToeicTestResponse create(ToeicTestRequest dto) {
        ToeicCollection collection =
                collectionRepository
                        .findById(dto.getCollectionId())
                        .orElseThrow(() -> new BusinessException(404, "Collection not found"));

        ToeicTest test = modelMapper.map(dto, ToeicTest.class);
        test.setCollection(collection);

        ToeicTest savedTest = testRepository.save(test);

        return modelMapper.map(savedTest, ToeicTestResponse.class);
    }

    public ToeicTestResponse update(Long id, ToeicTestRequest dto) {
        ToeicTest test =
                testRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Test not found"));

        modelMapper.map(dto, test);

        if (dto.getCollectionId() != null) {
            ToeicCollection collection =
                    collectionRepository
                            .findById(dto.getCollectionId())
                            .orElseThrow(() -> new BusinessException(404, "Collection not found"));
            test.setCollection(collection);
        }

        ToeicTest updatedToeicTest = testRepository.save(test);

        return modelMapper.map(updatedToeicTest, ToeicTestResponse.class);
    }

    public void delete(Long id) {
        boolean isExisted = testRepository.existsById(id);
        if (!isExisted) {
            throw new BusinessException(404, "Test not found");
        }

        testRepository.deleteById(id);
    }

}
