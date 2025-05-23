package com.ssafy.htrip.review.repository;

import com.ssafy.htrip.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // 특정 여행지의 리뷰 목록 조회
    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.attraction.placeId = :placeId")
    List<Review> findByAttractionIdWithUser(@Param("placeId") Integer placeId);

    // 특정 여행지의 리뷰 목록 조회 (페이징)
    Page<Review> findByAttractionPlaceId(Integer placeId, Pageable pageable);

    // 특정 사용자가 작성한 리뷰 목록 조회
    @Query("SELECT r FROM Review r JOIN FETCH r.attraction WHERE r.user.userId = :userId")
    List<Review> findByUserIdWithAttraction(@Param("userId") Integer userId);

    // 특정 사용자가 작성한 리뷰 목록 조회 (페이징)
    Page<Review> findByUserUserId(Integer userId, Pageable pageable);

    // 특정 사용자의 리뷰 개수 (통계용)
    Long countByUserUserId(Integer userId);

    // 특정 여행지의 리뷰 개수
    Long countByAttractionPlaceId(Integer placeId);
}