package com.ssafy.htrip.review.service;

import com.ssafy.htrip.review.dto.CreateReviewRequest;
import com.ssafy.htrip.review.dto.ReviewDto;
import com.ssafy.htrip.review.dto.UpdateReviewRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ReviewService {
    // 리뷰 생성
    ReviewDto createReview(Integer userId, CreateReviewRequest request);

    // 리뷰 수정
    ReviewDto updateReview(Integer userId, Integer reviewId, UpdateReviewRequest request);

    // 리뷰 삭제
    void deleteReview(Integer userId, Integer reviewId);

    // 특정 리뷰 조회
    ReviewDto getReview(Integer reviewId);

    // 특정 여행지의 리뷰 목록 조회 (페이징)
    Page<ReviewDto> getReviewsByPlaceId(Integer placeId, Pageable pageable);

    // 특정 사용자가 작성한 리뷰 목록 조회 (페이징)
    Page<ReviewDto> getReviewsByUserId(Integer userId, Pageable pageable);

    // 특정 여행지의 리뷰 통계 (리뷰 수 등)
    Map<String, Object> getReviewStatsByPlaceId(Integer placeId);
}