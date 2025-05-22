package com.ssafy.htrip.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatsDto {
    // 전체 통계
    private Long totalMembers;           // 전체 회원 수
    private Long totalActivePlans;       // 활성 여행 계획 수
    private Long totalAttractions;       // 총 관광지 수
    private Long totalFavorites;         // 총 찜 수

    // 최근 통계 (7일)
    private Long newMembersThisWeek;     // 이번 주 신규 가입자
    private Long newPlansThisWeek;       // 이번 주 신규 계획
    private Long activeMembersThisWeek;  // 이번 주 활성 사용자

    // 제공자별 통계
    private Map<String, Long> membersByProvider;  // OAuth 제공자별 회원 수

    // 역할별 통계
    private Long adminCount;             // 관리자 수
    private Long userCount;              // 일반 사용자 수

    // 시스템 정보
    private LocalDateTime lastUpdated;   // 마지막 업데이트 시간
}