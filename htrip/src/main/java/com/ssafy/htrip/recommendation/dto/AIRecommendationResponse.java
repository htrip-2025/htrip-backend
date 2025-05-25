package com.ssafy.htrip.recommendation.dto;

import com.ssafy.htrip.plan.dto.PlanDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIRecommendationResponse {
    private String recommendationId;   // 추천 ID (임시 저장용)
    private PlanDto recommendedPlan;   // 추천된 여행 계획
    private String reasoning;          // 추천 이유 설명
}