package com.ssafy.htrip.attraction.service;

import com.ssafy.htrip.attraction.dto.CategoryDto;
import com.ssafy.htrip.attraction.dto.ContentTypeDto;
import com.ssafy.htrip.attraction.entity.Category;
import com.ssafy.htrip.attraction.entity.ContentType;
import com.ssafy.htrip.attraction.repository.CategoryRepository;
import com.ssafy.htrip.attraction.repository.ContentTypeRepository;
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
public class CategoryServiceImpl implements CategoryService {

    private final ContentTypeRepository contentTypeRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<ContentTypeDto> getAllContentTypes() {
        return contentTypeRepository.findAll().stream()
                .map(this::toContentTypeDto)
                .collect(Collectors.toList());
    }

    @Override
    public ContentTypeDto getContentType(Integer contentTypeId) {
        ContentType contentType = contentTypeRepository.findById(contentTypeId)
                .orElse(null);
        return contentType != null ? toContentTypeDto(contentType) : null;
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(String category) {
        Category cat = categoryRepository.findById(category)
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
        categoryRepository.findById(categoryCode).ifPresent(category ->
                result.put(category.getCategory(), category.getCategoryName())
        );

        return result;
    }

    @Override
    public List<CategoryDto> getMainCategories() {
        // 길이가 3인 카테고리만 조회 (A01, A02, ...)
        return categoryRepository.findByCategoryLength(3).stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDto> getMiddleCategoriesByMain(String mainCategory) {
        if (mainCategory == null || mainCategory.length() != 3) {
            return Collections.emptyList();
        }

        // 특정 대분류로 시작하는 중분류 카테고리 조회
        return categoryRepository.findByCategoryStartingWithAndLength(mainCategory, 5).stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDto> getSubCategoriesByMiddle(String middleCategory) {
        if (middleCategory == null || middleCategory.length() != 5) {
            return Collections.emptyList();
        }

        // 특정 중분류로 시작하는 소분류 카테고리 조회
        return categoryRepository.findByCategoryStartingWithAndLength(middleCategory, 9).stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
    }

    // 변환 메서드
    private ContentTypeDto toContentTypeDto(ContentType contentType) {
        return new ContentTypeDto(
                contentType.getContentTypeId(),
                contentType.getContentName()
        );
    }

    private CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getCategory(),
                category.getCategoryName()
        );
    }
}