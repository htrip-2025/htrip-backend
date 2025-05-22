package com.ssafy.htrip.admin.dto;

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
public class AdminMemberDto {
    private Integer userId;
    private String email;
    private String name;
    private String nickname;
    private String profileImgUrl;
    private OauthProvider oauthProvider;
    private String oauthId;
    private Role role;
    private LocalDateTime registDate;
    private LocalDateTime lastLoginAt;

    // 통계 정보
    private Long favoriteCount;      // 찜한 여행지 수
    private Long planCount;          // 만든 여행 계획 수
    private Long loginCount;         // 로그인 횟수 (향후 확장)

    // 관리 정보
    private Boolean isActive;        // 계정 활성 상태
    private String lastAccessIp;     // 마지막 접속 IP (향후 확장)
}