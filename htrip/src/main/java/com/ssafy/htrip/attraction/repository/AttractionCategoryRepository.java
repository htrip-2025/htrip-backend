package com.ssafy.htrip.attraction.repository;

import com.ssafy.htrip.attraction.entity.AttractionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttractionCategoryRepository extends JpaRepository<AttractionCategory, String> {
    // 특정 길이의 카테고리 코드 조회
    @Query("SELECT c FROM AttractionCategory c WHERE LENGTH(c.category) = :length")
    List<AttractionCategory> findByCategoryLength(@Param("length") int length);

    // 특정 접두어로 시작하는 카테고리 조회
    @Query("SELECT c FROM AttractionCategory c WHERE c.category LIKE :prefix%")
    List<AttractionCategory> findByCategoryStartingWith(@Param("prefix") String prefix);

    // 특정 접두어로 시작하고 특정 길이를 가진 카테고리 조회
    @Query("SELECT c FROM AttractionCategory c WHERE c.category LIKE :prefix% AND LENGTH(c.category) = :length")
    List<AttractionCategory> findByCategoryStartingWithAndLength(
            @Param("prefix") String prefix, @Param("length") int length);
}