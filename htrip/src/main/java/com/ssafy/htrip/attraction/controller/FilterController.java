package com.ssafy.htrip.attraction.controller;

import com.ssafy.htrip.attraction.dto.AttractionCategoryDto;
import com.ssafy.htrip.attraction.dto.AttractionContentTypeDto;
import com.ssafy.htrip.attraction.service.AttractionCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/filters")
@RequiredArgsConstructor
@Tag(name = "Filters", description = "여행지 필터 조건 API")
public class FilterController {

    private final AttractionCategoryService attractionCategoryService;

    @Operation(summary = "컨텐츠 타입 목록 조회", description = "여행지 검색 필터에 사용할 컨텐츠 타입 목록을 조회합니다")
    @GetMapping("/content-types")
    public ResponseEntity<List<AttractionContentTypeDto>> getContentTypes() {
        List<AttractionContentTypeDto> contentTypes = attractionCategoryService.getAllContentTypes();
        return ResponseEntity.ok(contentTypes);
    }

    @Operation(summary = "대분류 카테고리 목록 조회", description = "여행지 검색 필터에 사용할 대분류 카테고리 목록을 조회합니다")
    @GetMapping("/categories/main")
    public ResponseEntity<List<AttractionCategoryDto>> getMainCategories() {
        List<AttractionCategoryDto> mainCategories = attractionCategoryService.getMainCategories();
        return ResponseEntity.ok(mainCategories);
    }

    @Operation(summary = "중분류 카테고리 목록 조회", description = "특정 대분류에 속한 중분류 카테고리 목록을 조회합니다")
    @GetMapping("/categories/middle")
    public ResponseEntity<List<AttractionCategoryDto>> getMiddleCategories(
            @RequestParam String mainCategory) {
        List<AttractionCategoryDto> middleCategories = attractionCategoryService.getMiddleCategoriesByMain(mainCategory);
        return ResponseEntity.ok(middleCategories);
    }

    @Operation(summary = "소분류 카테고리 목록 조회", description = "특정 중분류에 속한 소분류 카테고리 목록을 조회합니다")
    @GetMapping("/categories/sub")
    public ResponseEntity<List<AttractionCategoryDto>> getSubCategories(
            @RequestParam String middleCategory) {
        List<AttractionCategoryDto> subCategories = attractionCategoryService.getSubCategoriesByMiddle(middleCategory);
        return ResponseEntity.ok(subCategories);
    }
}