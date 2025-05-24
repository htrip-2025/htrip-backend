package com.ssafy.htrip.attraction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStructureDto {
    private String category;          // 카테고리 코드 (A01)
    private String categoryName;      // 카테고리 이름 (자연)
    private List<MiddleCategoryDto> children;  // 중분류 목록

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MiddleCategoryDto {
        private String category;          // 카테고리 코드 (A0101)
        private String categoryName;      // 카테고리 이름 (자연관광지)
        private List<SubCategoryDto> children;  // 소분류 목록
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubCategoryDto {
        private String category;          // 카테고리 코드 (A01010100)
        private String categoryName;      // 카테고리 이름 (산)
    }
}