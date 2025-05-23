package com.ssafy.htrip.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberStatsDto {
    private Long favoriteCount;       // 찜한 여행지 수
    private Long planCount;           // 만든 여행 계획 수
    private Long completedPlanCount;  // 완료된 여행 계획
    private Long reviewCount;         // 작성한 리뷰 수
    //private Long boardCount;        // 작성한 게시글 수
    private LocalDateTime joinDate;   // 가입일
    private Long daysSinceJoin;       // 가입 후 일수
}