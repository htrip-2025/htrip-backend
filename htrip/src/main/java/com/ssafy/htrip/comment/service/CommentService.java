// src/main/java/com/example/demo/service/CommentService.java
package com.ssafy.htrip.comment.service;

import com.ssafy.htrip.comment.dto.CommentRequestDto;
import com.ssafy.htrip.comment.dto.CommentResponseDto;

import java.util.List;

public interface CommentService {
    CommentResponseDto create(Long boardId, CommentRequestDto dto);
    List<CommentResponseDto> findByBoardId(Long boardId);
    CommentResponseDto findById(Long commentId);
    CommentResponseDto update(Long commentId, CommentRequestDto dto);
    void delete(Long commentId);
}
