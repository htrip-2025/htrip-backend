package com.ssafy.htrip.member.service;

import com.ssafy.htrip.member.dto.MemberProfileDto;
import com.ssafy.htrip.member.dto.MemberStatsDto;
import com.ssafy.htrip.member.dto.UpdateProfileRequest;

public interface MemberService {

    // 프로필 조회
    MemberProfileDto getProfile(Integer userId);

    // 프로필 수정
    MemberProfileDto updateProfile(Integer userId, UpdateProfileRequest request);

    // 마이페이지 통계 조회
    MemberStatsDto getStats(Integer userId);

    // 계정 삭제 (탈퇴)
    void deleteAccount(Integer userId);

    // 로그인 시간 업데이트 (이미 AuthService에 있지만 명시적으로)
    void updateLastLoginAt(Integer userId);
}