package com.ssafy.htrip.board.service;

import com.ssafy.htrip.board.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardService {
    // 게시글 생성
    BoardResponseDto createBoard(Integer userId, BoardRequestDto dto, List<MultipartFile> files) throws Exception;

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
    BoardResponseDto updateBoard(Integer userId, Long boardNo, BoardRequestDto dto, List<MultipartFile> files);

    // 게시글 삭제
    void deleteBoard(Integer userId, Long boardNo);

    // 좋아요 추가/취소
    void toggleLike(Integer userId, Long boardNo);

    // 내가 좋아요한 게시글 목록 조회
    Page<BoardResponseDto> getLikedBoards(Integer userId, Pageable pageable);

    // 게시글에 이미지 추가
    List<BoardImageDto> addImagesToBoard(Long boardNo, List<MultipartFile> files);

    // 특정 게시글의 이미지 조회
    List<BoardImageDto> getBoardImages(Long boardNo);

    // 특정 이미지 삭제
    void deleteImage(Long imageId);
}