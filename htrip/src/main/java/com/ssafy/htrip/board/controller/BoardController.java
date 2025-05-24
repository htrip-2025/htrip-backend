// src/main/java/com/example/demo/controller/BoardController.java
package com.ssafy.htrip.board.controller;

import com.ssafy.htrip.board.dto.BoardRequestDto;
import com.ssafy.htrip.board.dto.BoardResponseDto;
import com.ssafy.htrip.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResponseDto> create(@RequestBody BoardRequestDto dto) {
        return ResponseEntity
                .ok(boardService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDto> getOne(@PathVariable Long id) {
        return ResponseEntity
                .ok(boardService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAll() {
        return ResponseEntity
                .ok(boardService.findAll());
    }

    @PutMapping("/")
    public ResponseEntity<BoardResponseDto> update(@PathVariable Long id,
                                                   @RequestBody BoardRequestDto dto) {
        return ResponseEntity
                .ok(boardService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
