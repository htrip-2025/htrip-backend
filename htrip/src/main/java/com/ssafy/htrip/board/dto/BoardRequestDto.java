package com.ssafy.htrip.board.dto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoardRequestDto {
    @NotBlank(message = "제목은 필수 입력값입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String content;

    @NotNull(message = "카테고리는 필수 선택값입니다.")
    private Integer categoryNo;

    private Boolean isNotice;

    // 이미지 관련 필드 (필요시)
    private List<String> imageUrls;

    // 태그 관련 필드 (필요시)
    private List<String> tags;
}