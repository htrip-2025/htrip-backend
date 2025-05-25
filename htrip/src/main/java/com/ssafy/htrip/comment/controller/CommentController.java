// src/main/java/com/example/demo/controller/CommentController.java
package com.ssafy.htrip.comment.controller;

import com.ssafy.htrip.auth.dto.CustomOAuth2User;
import com.ssafy.htrip.comment.dto.CommentRequestDto;
import com.ssafy.htrip.comment.dto.CommentResponseDto;
import com.ssafy.htrip.comment.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
@Tag(name = "Comment", description = "댓글 관리 API")
public class CommentController {

    private final CommentService commentService;

    // 특정 게시글의 댓글 목록 조회
    @GetMapping("/{boardId}/comments")
    public ResponseEntity<Page<CommentResponseDto>> listByBoard(
                @PathVariable Long boardId,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size,
                @RequestParam(defaultValue = "writeDate") String sort,
                @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return ResponseEntity.ok(commentService.getCommentsByBoardNo(boardId, pageable));
    }

    // 댓글 작성
    @PostMapping("{boardId}/comments")
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
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDto> update(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody CommentRequestDto dto) {
        return ResponseEntity.ok(commentService.updateComment(id, user.getUserId(), dto));
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal CustomOAuth2User user) {
        commentService.deleteComment(id,user.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<Page<CommentResponseDto>> listByBoard(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "writeDate") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<CommentResponseDto> response = commentService.getMyComments(user.getUserId(), pageable);
        return ResponseEntity.ok(response);

    }
}
