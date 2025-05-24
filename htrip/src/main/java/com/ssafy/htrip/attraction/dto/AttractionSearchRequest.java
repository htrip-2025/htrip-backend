package com.ssafy.htrip.attraction.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AttractionSearchRequest {
    private String keyword;        // 키워드 검색
    private Integer areaCode;      // 지역 코드
    private Integer sigunguCode;   // 시군구 코드
    private String contentTypeId;  // 컨텐츠 타입 ID

    // 카테고리 정보
    private String categoryCode;   // 선택된 최종 카테고리 코드 (대분류, 중분류, 소분류 중 가장 상세한 것)
}