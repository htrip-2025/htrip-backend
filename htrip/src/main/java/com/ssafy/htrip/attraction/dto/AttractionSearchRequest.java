package com.ssafy.htrip.attraction.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class AttractionSearchRequest {
    private String keyword;        // 키워드 검색
    private Integer areaCode;      // 지역 코드
    private Integer sigunguCode;   // 시군구 코드
}