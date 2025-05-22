package com.ssafy.htrip.admin.service;

import com.ssafy.htrip.admin.dto.AdminCheckDto;
import com.ssafy.htrip.admin.dto.AdminMemberDto;
import com.ssafy.htrip.admin.dto.AdminStatsDto;
import com.ssafy.htrip.admin.dto.UpdateMemberRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface AdminService {

    AdminCheckDto getMember(Integer userId);

    // 회원 관리
    Page<AdminMemberDto> getAllMembers(String keyword, String provider, String role, Pageable pageable);
    AdminMemberDto getMemberDetail(Integer userId);
    AdminMemberDto updateMember(Integer userId, UpdateMemberRequest request);
    void deleteMember(Integer userId);

    // 통계 및 대시보드
    AdminStatsDto getAdminStats();
    Map<String, Long> getDailyRegistrations(int days);
    Map<String, Long> getOAuthProviderStats();
}