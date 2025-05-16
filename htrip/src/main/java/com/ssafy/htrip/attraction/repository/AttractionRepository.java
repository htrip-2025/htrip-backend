package com.ssafy.htrip.attraction.repository;

import com.ssafy.htrip.attraction.entity.Attraction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;

public interface AttractionRepository extends JpaRepository<Attraction, Integer> {
    // 특정 areaCode 기준 N개
    @Query("SELECT a FROM Attraction a WHERE a.area.areaCode = :code ORDER BY a.placeId DESC")
    List<Attraction> findTopNByAreaAreaCode(@Param("code") Integer areaCode, Pageable pageable);

    // 랜덤 N개 (MySQL RAND())
    @Query(value = "SELECT * FROM attraction ORDER BY RAND() LIMIT :n", nativeQuery = true)
    List<Attraction> findRandom(@Param("n") int n);

    // 페이징 지원
    Page<Attraction> findAll(Pageable pageable);

    List<Attraction> findByTitleContainingIgnoreCase(String keyword);
}
