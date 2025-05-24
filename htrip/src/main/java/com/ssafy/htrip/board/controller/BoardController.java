package com.ssafy.htrip.board.controller;

import com.ssafy.htrip.auth.dto.CustomOAuth2User;
import com.ssafy.htrip.board.dto.BoardRequestDto;
import com.ssafy.htrip.board.dto.BoardResponseDto;
import com.ssafy.htrip.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Tag(name = "Board", description = "게시판 API")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(
            @AuthenticationPrincipal CustomOAuth2User user,
            @Valid @RequestBody BoardRequestDto dto) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BoardResponseDto response = boardService.createBoard(user.getUserId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 번호로 게시글 상세 정보를 조회합니다.")
    @GetMapping("/{boardNo}")
    public ResponseEntity<BoardResponseDto> getBoardDetail(@PathVariable Long boardNo) {
        BoardResponseDto response = boardService.getBoardDetail(boardNo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 페이징하여 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<BoardResponseDto>> getBoards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "writeDate") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<BoardResponseDto> response = boardService.getBoards(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "공지사항 목록 조회", description = "공지사항 목록을 조회합니다.")
    @GetMapping("/notice")
    public ResponseEntity<List<BoardResponseDto>> getNotices() {
        List<BoardResponseDto> response = boardService.getNotices();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "카테고리별 게시글 목록 조회", description = "카테고리별 게시글 목록을 페이징하여 조회합니다.")
    @GetMapping("/category/{categoryNo}")
    public ResponseEntity<Page<BoardResponseDto>> getBoardsByCategory(
            @PathVariable Integer categoryNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "writeDate") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<BoardResponseDto> response = boardService.getBoardsByCategory(categoryNo, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 검색", description = "게시글을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<Page<BoardResponseDto>> searchBoards(
            @RequestParam String type,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "writeDate") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<BoardResponseDto> response = boardService.searchBoards(type, keyword, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 게시글 목록 조회", description = "내가 작성한 게시글 목록을 페이징하여 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<Page<BoardResponseDto>> getMyBoards(
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

        Page<BoardResponseDto> response = boardService.getMyBoards(user.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    @PutMapping("/{boardNo}")
    public ResponseEntity<BoardResponseDto> updateBoard(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Long boardNo,
            @Valid @RequestBody BoardRequestDto dto) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BoardResponseDto response = boardService.updateBoard(user.getUserId(), boardNo, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("/{boardNo}")
    public ResponseEntity<Void> deleteBoard(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Long boardNo) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boardService.deleteBoard(user.getUserId(), boardNo);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시글 좋아요", description = "게시글에 좋아요를 추가/취소합니다.")
    @PostMapping("/{boardNo}/like")
    public ResponseEntity<Void> toggleLike(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Long boardNo) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boardService.toggleLike(user.getUserId(), boardNo);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내가 좋아요한 게시글 목록 조회", description = "내가 좋아요한 게시글 목록을 페이징하여 조회합니다.")
    @GetMapping("/liked")
    public ResponseEntity<Page<BoardResponseDto>> getLikedBoards(
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

        Page<BoardResponseDto> response = boardService.getLikedBoards(user.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }
}