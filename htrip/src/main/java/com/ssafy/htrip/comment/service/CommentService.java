package com.ssafy.htrip.comment.service;

import com.ssafy.htrip.comment.dto.CommentRequestDto;
import com.ssafy.htrip.comment.dto.CommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    // 댓글 생성
    CommentResponseDto createComment(Long boardNo, Integer userId, CommentRequestDto dto);

    // 특정 게시글의 댓글 목록 조회
    List<CommentResponseDto> getCommentsByBoardNo(Long boardNo);

    // 특정 게시글의 댓글 목록 조회 (페이징)
    Page<CommentResponseDto> getCommentsByBoardNo(Long boardNo, Pageable pageable);

    // 내 댓글 목록 조회
    Page<CommentResponseDto> getMyComments(Integer userId, Pageable pageable);

    // 댓글 수정
    CommentResponseDto updateComment(Long commentId, Integer userId, CommentRequestDto dto);

    // 댓글 삭제
    void deleteComment(Long commentId, Integer userId);

    // 좋아요 추가/취소
    void toggleLike(Long commentId, Integer userId);
}