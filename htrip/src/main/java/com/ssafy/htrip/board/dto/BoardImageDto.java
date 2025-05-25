package com.ssafy.htrip.board.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoardImageDto {
    private Long imageId;
    private String imagePath;
    private String originalFileName;
    private String storedFileName;
    private Long fileSize;
    private Integer orderNum;
}