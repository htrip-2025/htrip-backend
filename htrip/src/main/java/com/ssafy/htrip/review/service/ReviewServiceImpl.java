package com.ssafy.htrip.review.service;

import com.ssafy.htrip.attraction.entity.Attraction;
import com.ssafy.htrip.attraction.repository.AttractionRepository;
import com.ssafy.htrip.common.entity.User;
import com.ssafy.htrip.common.repository.UserRepository;
import com.ssafy.htrip.review.dto.CreateReviewRequest;
import com.ssafy.htrip.review.dto.ReviewDto;
import com.ssafy.htrip.review.dto.UpdateReviewRequest;
import com.ssafy.htrip.review.entity.Review;
import com.ssafy.htrip.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final AttractionRepository attractionRepository;

    @Override
    @Transactional
    public ReviewDto createReview(Integer userId, CreateReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        Attraction attraction = attractionRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new EntityNotFoundException("Attraction not found: " + request.getPlaceId()));

        Review review = new Review();
        review.setUser(user);
        review.setAttraction(attraction);
        review.setContent(request.getContent());

        Review savedReview = reviewRepository.save(review);
        log.info("Created review: {}", savedReview.getReviewId());
        return toDto(savedReview);
    }

    @Override
    @Transactional
    public ReviewDto updateReview(Integer userId, Integer reviewId, UpdateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found: " + reviewId));

        // 작성자 본인만 수정 가능
        if (!review.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to update this review");
        }

        review.setContent(request.getContent());

        Review updatedReview = reviewRepository.save(review);
        log.info("Updated review: {}", updatedReview.getReviewId());
        return toDto(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Integer userId, Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found: " + reviewId));

        // 작성자 본인만 삭제 가능
        if (!review.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to delete this review");
        }

        reviewRepository.delete(review);
        log.info("Deleted review: {}", reviewId);
    }

    @Override
    public ReviewDto getReview(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found: " + reviewId));
        return toDto(review);
    }

    @Override
    public Page<ReviewDto> getReviewsByPlaceId(Integer placeId, Pageable pageable) {
        log.debug("Fetching reviews for place: {}, page: {}", placeId, pageable.getPageNumber());
        Page<Review> reviews = reviewRepository.findByAttractionPlaceId(placeId, pageable);
        return reviews.map(this::toDto);
    }

    @Override
    public Page<ReviewDto> getReviewsByUserId(Integer userId, Pageable pageable) {
        log.debug("Fetching reviews for user: {}, page: {}", userId, pageable.getPageNumber());
        Page<Review> reviews = reviewRepository.findByUserUserId(userId, pageable);
        return reviews.map(this::toDto);
    }

    @Override
    public Map<String, Object> getReviewStatsByPlaceId(Integer placeId) {
        Long reviewCount = reviewRepository.countByAttractionPlaceId(placeId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("reviewCount", reviewCount);

        return stats;
    }

    // 엔티티를 DTO로 변환하는 메서드
    private ReviewDto toDto(Review review) {
        return ReviewDto.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUser().getUserId())
                .userName(review.getUser().getNickname())
                .userProfileImage(review.getUser().getProfileImgUrl())
                .placeId(review.getAttraction().getPlaceId())
                .placeName(review.getAttraction().getTitle())
                .content(review.getContent())
                .createDate(review.getCreateDate())
                .updateDate(review.getUpdateDate())
                .build();
    }
}