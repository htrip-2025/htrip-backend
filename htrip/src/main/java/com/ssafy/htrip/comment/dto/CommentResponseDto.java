// src/main/java/com/example/demo/dto/CommentResponseDto.java
package com.ssafy.htrip.comment.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommentResponseDto {
    private Long commentId;
    private Long boardId;
    private Integer userId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
