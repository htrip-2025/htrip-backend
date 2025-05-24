// src/main/java/com/example/demo/service/impl/CommentServiceImpl.java
package com.ssafy.htrip.comment.service;


import com.ssafy.htrip.board.entity.Board;
import com.ssafy.htrip.board.repository.BoardRepository;
import com.ssafy.htrip.comment.dto.CommentRequestDto;
import com.ssafy.htrip.comment.dto.CommentResponseDto;
import com.ssafy.htrip.comment.entity.Comment;
import com.ssafy.htrip.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Override
    @Transactional
    public CommentResponseDto create(Long boardId, CommentRequestDto dto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));
        Comment comment = Comment.builder()
                .board(board)
                .userId(dto.getUserId())
                .content(dto.getContent())
                .build();
        Comment saved = commentRepository.save(comment);
        return toDto(saved);
    }

    @Override
    public List<CommentResponseDto> findByBoardId(Long boardId) {
        return commentRepository.findByBoardBoardId(boardId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto findById(Long commentId) {
        return commentRepository.findById(commentId)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    }

    @Override
    @Transactional
    public CommentResponseDto update(Long commentId, CommentRequestDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        comment.setContent(dto.getContent());
        // updatedAt 는 @PreUpdate로 자동 갱신
        return toDto(comment);
    }

    @Override
    @Transactional
    public void delete(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private CommentResponseDto toDto(Comment c) {
        return CommentResponseDto.builder()
                .commentId(c.getCommentId())
                .boardId(c.getBoard().getBoardId())
                .userId(c.getUserId())
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
