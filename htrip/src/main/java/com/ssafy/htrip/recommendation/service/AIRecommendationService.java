package com.ssafy.htrip.recommendation.service;

import com.ssafy.htrip.recommendation.dto.AIRecommendationRequest;
import com.ssafy.htrip.recommendation.dto.AIRecommendationResponse;

/**
 * 라우터 스테이트 방식을 위한 최적화된 추천 서비스 인터페이스
 * - 임시 저장소를 사용하지 않고 stateless 서비스 제공
 */
public interface AIRecommendationService {
    /**
     * 사용자 요청에 따른 AI 여행 경로 추천 생성
     * - 임시 저장 없이 결과만 반환 (프론트엔드에서 라우터 상태로 관리)
     */
    AIRecommendationResponse generateRecommendationPlan(AIRecommendationRequest request);
}