package com.ssafy.htrip.board.repository;

import com.ssafy.htrip.board.entity.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {
    // 특정 게시글의 모든 이미지 조회
    List<BoardImage> findByBoardBoardNoOrderByOrderNumAsc(Long boardNo);

    // 특정 게시글의 이미지 삭제
    void deleteByBoardBoardNo(Long boardNo);

    // 특정 게시글의 이미지 개수 조회
    Long countByBoardBoardNo(Long boardNo);
}