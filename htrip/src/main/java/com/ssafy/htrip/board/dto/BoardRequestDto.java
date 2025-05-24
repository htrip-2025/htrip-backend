// src/main/java/com/example/demo/dto/BoardRequestDto.java
package com.ssafy.htrip.board.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoardRequestDto {
    private Integer categoryNo;
    private Integer authorId;
    private String content;
}
