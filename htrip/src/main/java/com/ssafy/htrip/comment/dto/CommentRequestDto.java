// src/main/java/com/example/demo/dto/CommentRequestDto.java
package com.ssafy.htrip.comment.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommentRequestDto {
    private Integer userId;
    private String content;
}
