package com.ssafy.htrip.review.controller;

import com.ssafy.htrip.auth.dto.CustomOAuth2User;
import com.ssafy.htrip.review.dto.CreateReviewRequest;
import com.ssafy.htrip.review.dto.ReviewDto;
import com.ssafy.htrip.review.dto.UpdateReviewRequest;
import com.ssafy.htrip.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Review", description = "리뷰 관리 API")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "새로운 리뷰를 작성합니다.")
    @PostMapping
    public ResponseEntity<ReviewDto> createReview(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody CreateReviewRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            ReviewDto createdReview = reviewService.createReview(user.getUserId(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (Exception e) {
            log.error("리뷰 생성 실패: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "리뷰 수정", description = "작성한 리뷰를 수정합니다.")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Integer reviewId,
            @RequestBody UpdateReviewRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            ReviewDto updatedReview = reviewService.updateReview(user.getUserId(), reviewId, request);
            return ResponseEntity.ok(updatedReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("리뷰 수정 실패: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "리뷰 삭제", description = "작성한 리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Integer reviewId) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            reviewService.deleteReview(user.getUserId(), reviewId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("리뷰 삭제 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "리뷰 조회", description = "특정 리뷰의 상세 정보를 조회합니다.")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable Integer reviewId) {
        try {
            ReviewDto review = reviewService.getReview(reviewId);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            log.error("리뷰 조회 실패: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "여행지별 리뷰 목록 조회", description = "특정 여행지에 대한 리뷰 목록을 페이징하여 조회합니다.")
    @GetMapping("/place/{placeId}")
    public ResponseEntity<Page<ReviewDto>> getReviewsByPlace(
            @PathVariable Integer placeId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 항목 수") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드") @RequestParam(defaultValue = "createDate") String sort,
            @Parameter(description = "정렬 방향 (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<ReviewDto> reviews = reviewService.getReviewsByPlaceId(placeId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "내가 작성한 리뷰 목록 조회", description = "로그인한 사용자가 작성한 리뷰 목록을 페이징하여 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<Page<ReviewDto>> getMyReviews(
            @AuthenticationPrincipal CustomOAuth2User user,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 항목 수") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드") @RequestParam(defaultValue = "createDate") String sort,
            @Parameter(description = "정렬 방향 (asc/desc)") @RequestParam(defaultValue = "desc") String direction) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<ReviewDto> reviews = reviewService.getReviewsByUserId(user.getUserId(), pageable);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "여행지별 리뷰 통계 조회", description = "특정 여행지에 대한 리뷰 통계를 조회합니다.")
    @GetMapping("/place/{placeId}/stats")
    public ResponseEntity<Map<String, Object>> getReviewStats(@PathVariable Integer placeId) {
        Map<String, Object> stats = reviewService.getReviewStatsByPlaceId(placeId);
        return ResponseEntity.ok(stats);
    }
}