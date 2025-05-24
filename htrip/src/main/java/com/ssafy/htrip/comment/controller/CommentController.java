// src/main/java/com/example/demo/controller/CommentController.java
package com.ssafy.htrip.comment.controller;

import com.ssafy.htrip.auth.dto.CustomOAuth2User;
import com.ssafy.htrip.comment.dto.CommentRequestDto;
import com.ssafy.htrip.comment.dto.CommentResponseDto;
import com.ssafy.htrip.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 특정 게시글의 댓글 목록 조회
    @GetMapping("/api/boards/{boardId}/comments")
    public ResponseEntity<List<CommentResponseDto>> listByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(commentService.getCommentsByBoardNo(boardId));
    }

    // 댓글 작성
    @PostMapping("/api/boards/{boardId}/comments")
    public ResponseEntity<CommentResponseDto> create(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody CommentRequestDto dto) {
        return ResponseEntity.ok(commentService.createComment(boardId, user.getUserId(), dto));
    }

//    // 댓글 단건 조회
//    @GetMapping("/api/comments/{id}")
//    public ResponseEntity<CommentResponseDto> getOne(@PathVariable Long id) {
//        return ResponseEntity.ok(commentService.getMyComments(id));
//    }

    // 댓글 수정
    @PutMapping("/api/comments/{id}")
    public ResponseEntity<CommentResponseDto> update(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody CommentRequestDto dto) {
        return ResponseEntity.ok(commentService.updateComment(id, user.getUserId(), dto));
    }

    // 댓글 삭제
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal CustomOAuth2User user) {
        commentService.deleteComment(id,user.getUserId());
        return ResponseEntity.noContent().build();
    }
}
