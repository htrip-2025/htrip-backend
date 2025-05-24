// src/main/java/com/example/demo/repository/CategoryRepository.java
package com.ssafy.htrip.board.repository;

import com.ssafy.htrip.board.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCategoryRepository extends JpaRepository<Category, Integer> {
    // 카테고리명으로 검색
    Category findByCategoryName(String categoryName);
}
