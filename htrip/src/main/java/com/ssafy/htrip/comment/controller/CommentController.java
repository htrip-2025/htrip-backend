// src/main/java/com/example/demo/controller/CommentController.java
package com.ssafy.htrip.comment.controller;

import com.ssafy.htrip.comment.dto.CommentRequestDto;
import com.ssafy.htrip.comment.dto.CommentResponseDto;
import com.ssafy.htrip.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 특정 게시글의 댓글 목록 조회
    @GetMapping("/api/boards/{boardId}/comments")
    public ResponseEntity<List<CommentResponseDto>> listByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(commentService.findByBoardId(boardId));
    }

    // 댓글 작성
    @PostMapping("/api/boards/{boardId}/comments")
    public ResponseEntity<CommentResponseDto> create(
            @PathVariable Long boardId,
            @RequestBody CommentRequestDto dto) {
        return ResponseEntity.ok(commentService.create(boardId, dto));
    }

    // 댓글 단건 조회
    @GetMapping("/api/comments/{id}")
    public ResponseEntity<CommentResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.findById(id));
    }

    // 댓글 수정
    @PutMapping("/api/comments/{id}")
    public ResponseEntity<CommentResponseDto> update(
            @PathVariable Long id,
            @RequestBody CommentRequestDto dto) {
        return ResponseEntity.ok(commentService.update(id, dto));
    }

    // 댓글 삭제
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
