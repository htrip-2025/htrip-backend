package com.ssafy.htrip.attraction.repository;

import com.ssafy.htrip.attraction.entity.Attraction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttractionRepository extends JpaRepository<Attraction, Integer> {
    // 특정 areaCode 기준 N개
    @Query("SELECT a FROM Attraction a WHERE a.areaCode = :code ORDER BY a.placeId DESC")
    List<Attraction> findTopNByAreaAreaCode(@Param("code") Integer areaCode, Pageable pageable);

    // 랜덤 N개 (MySQL RAND())
    @Query(value = "SELECT * FROM attraction ORDER BY RAND() LIMIT :n", nativeQuery = true)
    List<Attraction> findRandom(@Param("n") int n);

    // 페이징 지원
    Page<Attraction> findAll(Pageable pageable);

    List<Attraction> findByTitleContainingIgnoreCase(String keyword);

    Page<Attraction> findByAreaCode(Integer areaCode, Pageable pageable);

    // 시군구별 검색
    Page<Attraction> findByAreaCodeAndSigunguCode(Integer areaCode, Integer sigunguCode, Pageable pageable);

    // 키워드 + 지역 조합 검색
    Page<Attraction> findByTitleContainingIgnoreCaseAndAreaCode(String keyword, Integer areaCode, Pageable pageable);

    // 키워드 + 시군구 조합 검색
    Page<Attraction> findByTitleContainingIgnoreCaseAndAreaCodeAndSigunguCode(
            String keyword, Integer areaCode, Integer sigunguCode, Pageable pageable);

    // 전체 검색 (키워드만)
    Page<Attraction> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // 고급 필터링
    @Query("SELECT a FROM Attraction a WHERE " +
            "(:keyword IS NULL OR a.title LIKE %:keyword%) AND " +
            "(:areaCode IS NULL OR a.areaCode = :areaCode) AND " +
            "(:sigunguCode IS NULL OR a.sigunguCode = :sigunguCode) AND " +
            "(:contentTypeId IS NULL OR a.contentTypeId = :contentTypeId) AND " +
            "(:category1 IS NULL OR a.category1 = :category1) AND " +
            "(:category2 IS NULL OR a.category2 = :category2) AND " +
            "(:category3 IS NULL OR a.category3 = :category3)")
    Page<Attraction> findWithFilters(
            @Param("keyword") String keyword,
            @Param("areaCode") Integer areaCode,
            @Param("sigunguCode") Integer sigunguCode,
            @Param("contentTypeId") String contentTypeId,
            @Param("category1") String category1,
            @Param("category2") String category2,
            @Param("category3") String category3,
            Pageable pageable);
}
