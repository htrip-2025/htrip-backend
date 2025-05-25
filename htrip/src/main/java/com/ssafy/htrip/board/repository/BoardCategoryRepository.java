package com.ssafy.htrip.board.repository;

import com.ssafy.htrip.board.entity.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCategoryRepository extends JpaRepository<BoardCategory, Integer> {
    // 카테고리명으로 검색
    BoardCategory findByCategoryName(String categoryName);
}
