package com.ssafy.htrip.attraction.repository;

import com.ssafy.htrip.attraction.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {
    // 특정 길이의 카테고리 코드 조회
    @Query("SELECT c FROM Category c WHERE LENGTH(c.category) = :length")
    List<Category> findByCategoryLength(@Param("length") int length);

    // 특정 접두어로 시작하는 카테고리 조회
    @Query("SELECT c FROM Category c WHERE c.category LIKE :prefix%")
    List<Category> findByCategoryStartingWith(@Param("prefix") String prefix);

    // 특정 접두어로 시작하고 특정 길이를 가진 카테고리 조회
    @Query("SELECT c FROM Category c WHERE c.category LIKE :prefix% AND LENGTH(c.category) = :length")
    List<Category> findByCategoryStartingWithAndLength(
            @Param("prefix") String prefix, @Param("length") int length);
}