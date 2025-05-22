package com.ssafy.htrip.admin.controller;

import com.ssafy.htrip.admin.dto.AdminCheckDto;
import com.ssafy.htrip.auth.dto.CustomOAuth2User;
import com.ssafy.htrip.admin.dto.AdminMemberDto;
import com.ssafy.htrip.admin.dto.AdminStatsDto;
import com.ssafy.htrip.admin.dto.UpdateMemberRequest;
import com.ssafy.htrip.admin.service.AdminService;
import com.ssafy.htrip.common.entity.Role;
import com.ssafy.htrip.member.dto.MemberProfileDto;
import com.ssafy.htrip.member.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "관리자 전용 시스템 관리 API")
public class AdminController {

    private final AdminService adminService;
    private final MemberService memberService;

    // 권한 체크 헬퍼 메서드
    private boolean isAdmin(CustomOAuth2User user) {
        AdminCheckDto loginuser = adminService.getMember(user.getUserId());
        return loginuser.getRole().equals(Role.ADMIN);
    }

    // === 회원 관리 ===

    // 전체 회원 목록 조회 (페이징, 검색)
    @GetMapping("/members")
    public ResponseEntity<?> getAllMembers(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "registDate") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        if (!isAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "관리자 권한이 필요합니다."));
        }

        try {
            Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

            Page<AdminMemberDto> members = adminService.getAllMembers(keyword, provider, role, pageable);
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            log.error("회원 목록 조회 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "회원 목록 조회에 실패했습니다."));
        }
    }

    // 특정 회원 상세 정보 조회
    @GetMapping("/members/{userId}")
    public ResponseEntity<?> getMemberDetail(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Integer userId) {

        if (!isAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "관리자 권한이 필요합니다."));
        }

        try {
            AdminMemberDto member = adminService.getMemberDetail(userId);
            return ResponseEntity.ok(member);
        } catch (Exception e) {
            log.error("회원 상세 조회 실패: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "회원을 찾을 수 없습니다."));
        }
    }

    // 회원 정보 수정 (역할 변경 등)
    @PutMapping("/members/{userId}")
    public ResponseEntity<?> updateMember(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Integer userId,
            @RequestBody UpdateMemberRequest request) {

        MemberProfileDto loginuser = memberService.getProfile(user.getUserId());
        if (!loginuser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "관리자 권한이 필요합니다."));
        }

        try {
            AdminMemberDto updatedMember = adminService.updateMember(userId, request);
            return ResponseEntity.ok(updatedMember);
        } catch (Exception e) {
            log.error("회원 정보 수정 실패: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "회원 정보 수정에 실패했습니다."));
        }
    }

    // 회원 계정 삭제
    @DeleteMapping("/members/{userId}")
    public ResponseEntity<?> deleteMember(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Integer userId) {

        if (!isAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "관리자 권한이 필요합니다."));
        }

        // 자기 자신은 삭제 불가
        if (user.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "자기 자신의 계정은 삭제할 수 없습니다."));
        }

        try {
            adminService.deleteMember(userId);
            return ResponseEntity.ok(Map.of("message", "회원이 삭제되었습니다."));
        } catch (Exception e) {
            log.error("회원 삭제 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "회원 삭제에 실패했습니다."));
        }
    }

    // === 통계 및 대시보드 ===

    // 운영자 대시보드 통계
    @GetMapping("/stats")
    public ResponseEntity<?> getAdminStats(
            @AuthenticationPrincipal CustomOAuth2User user) {

        if (!isAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "관리자 권한이 필요합니다."));
        }

        try {
            AdminStatsDto stats = adminService.getAdminStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("통계 조회 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "통계 조회에 실패했습니다."));
        }
    }

    // 일별 가입자 추이 (최근 30일)
    @GetMapping("/stats/daily-registrations")
    public ResponseEntity<?> getDailyRegistrations(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestParam(defaultValue = "30") int days) {

        if (!isAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "관리자 권한이 필요합니다."));
        }

        try {
            Map<String, Long> dailyStats = adminService.getDailyRegistrations(days);
            return ResponseEntity.ok(dailyStats);
        } catch (Exception e) {
            log.error("일별 가입 통계 조회 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "통계 조회에 실패했습니다."));
        }
    }

    // OAuth 제공자별 통계
    @GetMapping("/stats/oauth-providers")
    public ResponseEntity<?> getOAuthProviderStats(
            @AuthenticationPrincipal CustomOAuth2User user) {

        if (!isAdmin(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "관리자 권한이 필요합니다."));
        }

        try {
            Map<String, Long> providerStats = adminService.getOAuthProviderStats();
            return ResponseEntity.ok(providerStats);
        } catch (Exception e) {
            log.error("OAuth 제공자 통계 조회 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "통계 조회에 실패했습니다."));
        }
    }
}
