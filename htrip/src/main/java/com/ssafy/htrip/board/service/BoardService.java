package com.ssafy.htrip.board.service;// src/main/java/com/example/demo/service/BoardService.java

import com.ssafy.htrip.board.dto.BoardRequestDto;
import com.ssafy.htrip.board.dto.BoardResponseDto;

import java.util.List;

public interface BoardService {
    BoardResponseDto create(BoardRequestDto dto);
    BoardResponseDto findById(Long boardId);
    List<BoardResponseDto> findAll();
    BoardResponseDto update(Long boardId, BoardRequestDto dto);
    void delete(Long boardId);
}
