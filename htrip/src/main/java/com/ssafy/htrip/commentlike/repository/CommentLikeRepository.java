package com.ssafy.htrip.commentlike.repository;


import com.ssafy.htrip.commentlike.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    // 특정 사용자가 특정 댓글에 좋아요를 눌렀는지 확인
    Optional<CommentLike> findByCommentCommentIdAndUserUserId(Long commentId, Integer userId);

    // 특정 댓글의 좋아요 개수 조회
    Long countByCommentCommentId(Long commentId);

    // 특정 사용자가 좋아요한 댓글 목록 조회
    List<CommentLike> findByUserUserId(Integer userId);

    // 특정 댓글의 좋아요를 누른 사용자 ID 목록 조회
    @Query("SELECT cl.user.userId FROM CommentLike cl WHERE cl.comment.commentId = :commentId")
    List<Integer> findUserIdsByCommentCommentId(@Param("commentId") Long commentId);

    // 특정 댓글의 좋아요 삭제 (댓글 삭제 시)
    void deleteByCommentCommentId(Long commentId);

    // 특정 게시글의 모든 댓글 좋아요 삭제 (게시글 삭제 시)
    @Query("DELETE FROM CommentLike cl WHERE cl.comment.board.boardNo = :boardNo")
    void deleteByBoardNo(@Param("boardNo") Long boardNo);

    // 특정 사용자의 모든 좋아요 삭제 (회원 탈퇴 시)
    void deleteByUserUserId(Integer userId);
}