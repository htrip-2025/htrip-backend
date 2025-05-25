package com.ssafy.htrip.board.service;

import com.ssafy.htrip.board.dto.BoardListWithNoticeDto;
import com.ssafy.htrip.board.dto.BoardRequestDto;
import com.ssafy.htrip.board.dto.BoardResponseDto;
import com.ssafy.htrip.board.dto.BoardSortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {
    // 게시글 생성
    BoardResponseDto createBoard(Integer userId, BoardRequestDto dto);

    // 게시글 상세 조회
    BoardResponseDto getBoardDetail(Long boardNo);

    // 게시글 목록 조회 (페이징)
    BoardListWithNoticeDto getBoards(BoardSortType sortType, Pageable pageable);

    // 카테고리별 게시글 목록 조회
    BoardListWithNoticeDto getBoardsByCategory(BoardSortType sortType, Integer categoryNo, Pageable pageable);

    // 검색 기능
    Page<BoardResponseDto> searchBoards(String type, String keyword, Pageable pageable);

    // 내 게시글 목록 조회
    Page<BoardResponseDto> getMyBoards(Integer userId, Pageable pageable);

    // 게시글 수정
    BoardResponseDto updateBoard(Integer userId, Long boardNo, BoardRequestDto dto);

    // 게시글 삭제
    void deleteBoard(Integer userId, Long boardNo);

    // 좋아요 추가/취소
    void toggleLike(Integer userId, Long boardNo);

    // 내가 좋아요한 게시글 목록 조회
    Page<BoardResponseDto> getLikedBoards(Integer userId, Pageable pageable);
}