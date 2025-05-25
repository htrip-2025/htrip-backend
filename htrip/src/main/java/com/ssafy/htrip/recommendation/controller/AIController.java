package com.ssafy.htrip.recommendation.controller;

import com.ssafy.htrip.recommendation.dto.AIRecommendationRequest;
import com.ssafy.htrip.recommendation.dto.AIRecommendationResponse;
import com.ssafy.htrip.recommendation.service.AIRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AIController {
    private final AIRecommendationService aiRecommendationService;

    @PostMapping()
    public ResponseEntity<AIRecommendationResponse> generateRecommendationPlan(@RequestBody AIRecommendationRequest request){
        AIRecommendationResponse recommendation = aiRecommendationService.generateRecommendationPlan(request);
        return ResponseEntity.ok(recommendation);
    }
}
