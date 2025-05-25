package com.ssafy.htrip.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIRecommendationRequest {
    // 지역 필터
    private Integer areaCode;

    // 기간
    private Integer durationDays;

    // 여행 구성원
    private String travelWith; // 혼자, 친구와, 커플, 배우자, 아이와, 부모님과, 단체

    // 여행 스타일 (다중 선택 가능)
    private String[] travelStyles; // 자연/풍경, 맛집/음식, 역사/문화, 쇼핑, 휴양/힐링, 액티비티, 사진촬영, 예술/공연, 캠핑, 테마파크

    // 원하는 일정 유형
    private String scheduleType; // 빡빡, 널널
}