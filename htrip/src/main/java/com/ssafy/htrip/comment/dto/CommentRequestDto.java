package com.ssafy.htrip.comment.dto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommentRequestDto {
    @NotBlank(message = "댓글 내용은 필수 입력값입니다.")
    private String content;
}