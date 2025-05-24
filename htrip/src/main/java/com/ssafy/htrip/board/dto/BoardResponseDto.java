package com.ssafy.htrip.board.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoardResponseDto {
    private Long boardNo;
    private String title;
    private String content;
    private Integer userId;
    private String author;
    private String profileImgUrl;
    private Integer categoryNo;
    private String category;
    private LocalDateTime writeDate;
    private LocalDateTime updateDate;
    private Integer views;
    private Integer likes;
    private Integer commentCount;
    private Boolean hasImage;
    private Boolean isNotice;

    // 이미지 관련 필드 (필요시)
    private List<String> imageUrls;

    // 태그 관련 필드 (필요시)
    private List<String> tags;
}