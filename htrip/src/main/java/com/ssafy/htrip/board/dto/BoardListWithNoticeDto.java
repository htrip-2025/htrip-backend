// com.ssafy.htrip.board.dto.BoardListWithNoticeDto.java
package com.ssafy.htrip.board.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter @Setter
public class BoardListWithNoticeDto {
    private BoardResponseDto latestNotice;    // 최신 공지사항 1개
    private Page<BoardResponseDto> boards;    // 일반 게시글 목록 (페이징)
    private BoardSortType sortType;           // 정렬 기준
}