package com.ssafy.htrip.member.dto;

import com.ssafy.htrip.common.entity.OauthProvider;
import com.ssafy.htrip.common.entity.Role;
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
public class MemberProfileDto {
    private Integer userId;
    private String email;
    private String name;
    private String nickname;
    private String profileImgUrl;
    private LocalDateTime registDate;
    private LocalDateTime lastLoginAt;
    private OauthProvider oauthProvider;
    private Role role;

    // 통계 정보
    private Long favoriteCount;      // 찜한 여행지 수
    private Long planCount;          // 만든 여행 계획 수
    //private Long visitedCount;       // 방문한 곳 수 (향후 확장)
}