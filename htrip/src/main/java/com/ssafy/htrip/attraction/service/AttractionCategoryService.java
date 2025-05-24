package com.ssafy.htrip.attraction.service;

import com.ssafy.htrip.attraction.dto.AttractionCategoryDto;
import com.ssafy.htrip.attraction.dto.AttractionContentTypeDto;

import java.util.List;
import java.util.Map;

public interface AttractionCategoryService {
    // 모든 컨텐츠 타입 조회
    List<AttractionContentTypeDto> getAllContentTypes();

    // 특정 컨텐츠 타입 조회
    AttractionContentTypeDto getContentType(Integer contentTypeId);

    // 모든 카테고리 조회
    List<AttractionCategoryDto> getAllCategories();

    // 특정 카테고리 조회
    AttractionCategoryDto getCategory(String category);

    // 관광지의 카테고리 정보 조회
    Map<String, String> getAttractionCategories(String categoryCode);

    // 대분류 카테고리 조회 (길이가 3인 카테고리: A01, A02, ...)
    List<AttractionCategoryDto> getMainCategories();

    // 특정 대분류에 속한 중분류 카테고리 조회
    List<AttractionCategoryDto> getMiddleCategoriesByMain(String mainCategory);

    // 특정 중분류에 속한 소분류 카테고리 조회
    List<AttractionCategoryDto> getSubCategoriesByMiddle(String middleCategory);
}