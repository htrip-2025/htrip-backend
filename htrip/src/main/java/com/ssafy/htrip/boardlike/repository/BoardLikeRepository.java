package com.ssafy.htrip.boardlike.repository;


import com.ssafy.htrip.boardlike.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    // 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    Optional<BoardLike> findByBoardBoardNoAndUserUserId(Long boardNo, Integer userId);

    // 특정 게시글의 좋아요 개수 조회
    Long countByBoardBoardNo(Long boardNo);

    // 특정 사용자가 좋아요한 게시글 목록 조회
    List<BoardLike> findByUserUserId(Integer userId);

    // 특정 게시글의 좋아요를 누른 사용자 ID 목록 조회
    @Query("SELECT bl.user.userId FROM BoardLike bl WHERE bl.board.boardNo = :boardNo")
    List<Integer> findUserIdsByBoardBoardNo(@Param("boardNo") Long boardNo);

    // 특정 사용자의 좋아요 삭제 (게시글 삭제 시)
    void deleteByBoardBoardNo(Long boardNo);

    // 특정 사용자의 모든 좋아요 삭제 (회원 탈퇴 시)
    void deleteByUserUserId(Integer userId);
}