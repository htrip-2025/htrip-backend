package com.ssafy.htrip.comment.repository;


import com.ssafy.htrip.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 게시글의 댓글 목록 조회
    List<Comment> findByBoardBoardNo(Long boardNo);

    // 특정 게시글의 댓글 목록 조회 (페이징)
    Page<Comment> findByBoardBoardNo(Long boardNo, Pageable pageable);

    // 특정 사용자가 작성한 댓글 조회
    Page<Comment> findByUserUserId(Integer userId, Pageable pageable);

    // 특정 사용자의 댓글 수 조회
    Long countByUserUserId(Integer userId);

    // 내용 검색
    Page<Comment> findByContentContaining(String keyword, Pageable pageable);
}