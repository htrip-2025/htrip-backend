package com.ssafy.htrip.favorite.repository;

import com.ssafy.htrip.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

    // 특정 사용자의 찜 목록 조회
    @Query("SELECT f FROM Favorite f JOIN FETCH f.attraction WHERE f.user.userId = :userId")
    List<Favorite> findByUserIdWithAttraction(@Param("userId") Integer userId);

    // 특정 사용자가 특정 장소를 찜했는지 확인
    @Query("SELECT f FROM Favorite f WHERE f.user.userId = :userId AND f.attraction.placeId = :placeId")
    Optional<Favorite> findByUserIdAndPlaceId(@Param("userId") Integer userId, @Param("placeId") Integer placeId);

    // 사용자-장소 조합으로 존재 여부 확인
    boolean existsByUserUserIdAndAttractionPlaceId(Integer userId, Integer placeId);

    // 특정 장소의 찜 개수
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.attraction.placeId = :placeId")
    Long countByPlaceId(@Param("placeId") Integer placeId);

    // 태그별 검색
    @Query("SELECT f FROM Favorite f JOIN FETCH f.attraction WHERE f.user.userId = :userId AND f.tag LIKE %:tag%")
    List<Favorite> findByUserIdAndTagContaining(@Param("userId") Integer userId, @Param("tag") String tag);
}