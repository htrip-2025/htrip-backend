package com.ssafy.htrip.comment.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommentResponseDto {
    private Long commentId;
    private Long boardNo;
    private String postTitle;
    private Integer userId;
    private String author;
    private String profileImgUrl;
    private String content;
    private LocalDateTime writeDate;
    private LocalDateTime updateDate;
    private Integer likes;
    private Boolean isLiked;  // 현재 로그인한 사용자가 좋아요를 눌렀는지 여부
}