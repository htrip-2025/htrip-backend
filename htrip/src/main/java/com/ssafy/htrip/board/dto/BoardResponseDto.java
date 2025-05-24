// src/main/java/com/example/demo/dto/BoardResponseDto.java
package com.ssafy.htrip.board.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoardResponseDto {
    private Long boardId;
    private Integer categoryNo;
    private Integer authorId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer views;
    private Integer recommendationCount;
}
