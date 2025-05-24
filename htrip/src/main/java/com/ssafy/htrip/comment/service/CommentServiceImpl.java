package com.ssafy.htrip.comment.service;

import com.ssafy.htrip.board.entity.Board;
import com.ssafy.htrip.board.repository.BoardRepository;
import com.ssafy.htrip.comment.dto.CommentRequestDto;
import com.ssafy.htrip.comment.dto.CommentResponseDto;
import com.ssafy.htrip.comment.entity.Comment;
import com.ssafy.htrip.comment.repository.CommentRepository;
import com.ssafy.htrip.commentlike.entity.CommentLike;
import com.ssafy.htrip.commentlike.repository.CommentLikeRepository;
import com.ssafy.htrip.common.entity.User;
import com.ssafy.htrip.common.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentResponseDto createComment(Long boardNo, Integer userId, CommentRequestDto dto) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .board(board)
                .user(user)
                .content(dto.getContent())
                .build();

        Comment savedComment = commentRepository.save(comment);

        // 게시글의 댓글 수 증가
        board.addComment(savedComment);

        return mapToDto(savedComment);
    }

    @Override
    public List<CommentResponseDto> getCommentsByBoardNo(Long boardNo) {
        return commentRepository.findByBoardBoardNo(boardNo).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CommentResponseDto> getCommentsByBoardNo(Long boardNo, Pageable pageable) {
        return commentRepository.findByBoardBoardNo(boardNo, pageable)
                .map(this::mapToDto);
    }

    @Override
    public Page<CommentResponseDto> getMyComments(Integer userId, Pageable pageable) {
        return commentRepository.findByUserUserId(userId, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long commentId, Integer userId, CommentRequestDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        // 작성자 검증
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("댓글 수정 권한이 없습니다.");
        }

        comment.setContent(dto.getContent());

        return mapToDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Integer userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        // 작성자 검증 (관리자는 별도 처리 필요)
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("댓글 삭제 권한이 없습니다.");
        }

        // 게시글의 댓글 수 감소
        Board board = comment.getBoard();
        board.removeComment(comment);

        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void toggleLike(Long commentId, Integer userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 이미 좋아요를 눌렀는지 확인
        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentCommentIdAndUserUserId(commentId, userId);

        if (existingLike.isPresent()) {
            // 이미 좋아요를 눌렀다면 좋아요 취소
            commentLikeRepository.delete(existingLike.get());
            comment.decreaseLikes();
        } else {
            // 좋아요를 누르지 않았다면 좋아요 추가
            CommentLike commentLike = CommentLike.builder()
                    .comment(comment)
                    .user(user)
                    .build();
            commentLikeRepository.save(commentLike);
            comment.increaseLikes();
        }
    }

    // Entity -> DTO 변환 메서드 (현재 로그인한 사용자의 좋아요 상태 반영)
    private CommentResponseDto mapToDto(Comment comment) {
        boolean isLiked = false;
        if (comment.getUser().getUserId() != null) {
            isLiked = commentLikeRepository.findByCommentCommentIdAndUserUserId(comment.getCommentId(), comment.getUser().getUserId()).isPresent();
        }

        return CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .boardNo(comment.getBoard().getBoardNo())
                .postTitle(comment.getBoard().getTitle())
                .userId(comment.getUser().getUserId())
                .author(comment.getUser().getNickname())
                .profileImgUrl(comment.getUser().getProfileImgUrl())
                .content(comment.getContent())
                .writeDate(comment.getWriteDate())
                .updateDate(comment.getUpdateDate())
                .likes(comment.getLikes())
                .isLiked(isLiked)  // 현재 사용자의 좋아요 상태 추가
                .build();
    }
}