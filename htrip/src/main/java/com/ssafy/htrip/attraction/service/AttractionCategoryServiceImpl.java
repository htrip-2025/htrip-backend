package com.ssafy.htrip.attraction.service;

import com.ssafy.htrip.attraction.dto.AttractionCategoryDto;
import com.ssafy.htrip.attraction.dto.AttractionContentTypeDto;
import com.ssafy.htrip.attraction.entity.AttractionCategory;
import com.ssafy.htrip.attraction.entity.AttractionContentType;
import com.ssafy.htrip.attraction.repository.AttractionCategoryRepository;
import com.ssafy.htrip.attraction.repository.AttractionContentTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttractionCategoryServiceImpl implements AttractionCategoryService {

    private final AttractionContentTypeRepository attractionContentTypeRepository;
    private final AttractionCategoryRepository attractionCategoryRepository;

    @Override
    public List<AttractionContentTypeDto> getAllContentTypes() {
        return attractionContentTypeRepository.findAll().stream()
                .map(this::toContentTypeDto)
                .collect(Collectors.toList());
    }

    @Override
    public AttractionContentTypeDto getContentType(Integer contentTypeId) {
        AttractionContentType attractionContentType = attractionContentTypeRepository.findById(contentTypeId)
                .orElse(null);
        return attractionContentType != null ? toContentTypeDto(attractionContentType) : null;
    }

    @Override
    public List<AttractionCategoryDto> getAllCategories() {
        return attractionCategoryRepository.findAll().stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public AttractionCategoryDto getCategory(String category) {
        AttractionCategory cat = attractionCategoryRepository.findById(category)
                .orElse(null);
        return cat != null ? toCategoryDto(cat) : null;
    }

    @Override
    public Map<String, String> getAttractionCategories(String categoryCode) {
        Map<String, String> result = new LinkedHashMap<>();

        if (categoryCode == null || categoryCode.isEmpty()) {
            return result;
        }

        // 카테고리 코드로 직접 조회
        attractionCategoryRepository.findById(categoryCode).ifPresent(attractionCategory ->
                result.put(attractionCategory.getCategory(), attractionCategory.getCategoryName())
        );

        return result;
    }

    @Override
    public List<AttractionCategoryDto> getMainCategories() {
        // 길이가 3인 카테고리만 조회 (A01, A02, ...)
        return attractionCategoryRepository.findByCategoryLength(3).stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttractionCategoryDto> getMiddleCategoriesByMain(String mainCategory) {
        if (mainCategory == null || mainCategory.length() != 3) {
            return Collections.emptyList();
        }

        // 특정 대분류로 시작하는 중분류 카테고리 조회
        return attractionCategoryRepository.findByCategoryStartingWithAndLength(mainCategory, 5).stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttractionCategoryDto> getSubCategoriesByMiddle(String middleCategory) {
        if (middleCategory == null || middleCategory.length() != 5) {
            return Collections.emptyList();
        }

        // 특정 중분류로 시작하는 소분류 카테고리 조회
        return attractionCategoryRepository.findByCategoryStartingWithAndLength(middleCategory, 9).stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
    }

    // 변환 메서드
    private AttractionContentTypeDto toContentTypeDto(AttractionContentType attractionContentType) {
        return new AttractionContentTypeDto(
                attractionContentType.getContentTypeId(),
                attractionContentType.getContentName()
        );
    }

    private AttractionCategoryDto toCategoryDto(AttractionCategory attractionCategory) {
        return new AttractionCategoryDto(
                attractionCategory.getCategory(),
                attractionCategory.getCategoryName()
        );
    }
}