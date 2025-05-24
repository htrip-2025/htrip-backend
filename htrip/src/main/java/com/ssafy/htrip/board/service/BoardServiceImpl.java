// src/main/java/com/example/demo/service/impl/BoardServiceImpl.java
package com.ssafy.htrip.board.service;

import com.ssafy.htrip.board.dto.BoardRequestDto;
import com.ssafy.htrip.board.dto.BoardResponseDto;
import com.ssafy.htrip.board.entity.Board;
import com.ssafy.htrip.board.entity.Category;
import com.ssafy.htrip.board.repository.BoardRepository;
import com.ssafy.htrip.board.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public BoardResponseDto create(BoardRequestDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryNo())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category"));
        Board board = Board.builder()
                .category(category)
                .authorId(dto.getAuthorId())
                .content(dto.getContent())
                .build();
        Board saved = boardRepository.save(board);
        return toDto(saved);
    }

    @Override
    public BoardResponseDto findById(Long boardId) {
        return boardRepository.findById(boardId)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));
    }

    @Override
    public List<BoardResponseDto> findAll() {
        return boardRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BoardResponseDto update(Long boardId, BoardRequestDto dto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));
        Category category = categoryRepository.findById(dto.getCategoryNo())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category"));
        board.setCategory(category);
        board.setContent(dto.getContent());
        // updatedAt 자동 갱신 via @PreUpdate
        return toDto(board);
    }

    @Override
    @Transactional
    public void delete(Long boardId) {
        boardRepository.deleteById(boardId);
    }

    private BoardResponseDto toDto(Board b) {
        return BoardResponseDto.builder()
                .boardId(b.getBoardId())
                .categoryNo(b.getCategory().getCategoryNo())
                .authorId(b.getAuthorId())
                .content(b.getContent())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .views(b.getViews())
                .recommendationCount(b.getRecommendationCount())
                .build();
    }
}
