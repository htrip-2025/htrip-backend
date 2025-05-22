package com.ssafy.htrip.plan.controller;

import com.ssafy.htrip.auth.dto.CustomOAuth2User;
import com.ssafy.htrip.plan.dto.*;
import com.ssafy.htrip.plan.service.PlanService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
@Tag(name = "Plan", description = "여행 계획 관리 API")
public class PlanController {

    private final PlanService planService;

    // 단건 조회
    @GetMapping("/{planId}")
    public ResponseEntity<PlanDto> getPlan(@PathVariable Integer planId) {
        try {
            PlanDto dto = planService.getPlan(planId);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 권한 정보 포함 조회
    @GetMapping("/{planId}/with-permission")
    public ResponseEntity<PlanDto> getPlanWithPermission(
            @PathVariable Integer planId,
            @AuthenticationPrincipal CustomOAuth2User user) {

        try {
            Integer userId = user != null ? user.getUserId() : null;
            PlanDto dto = planService.getPlanWithPermission(planId, userId);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 전체 목록 조회 (공개된 계획만)
    @GetMapping
    public ResponseEntity<List<PlanDto>> getAllPlans() {
        List<PlanDto> list = planService.getAllPlans();
        return ResponseEntity.ok(list);
    }

    // 내가 참여한 계획 목록 조회
    @GetMapping("/my")
    public ResponseEntity<List<PlanDto>> getMyPlans(
            @AuthenticationPrincipal CustomOAuth2User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<PlanDto> myPlans = planService.getMyPlans(user.getUserId());
        return ResponseEntity.ok(myPlans);
    }

    // 일정 생성
    @PostMapping
    public ResponseEntity<PlanDto> createPlan(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody CreatePlanRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            PlanDto createdPlan = planService.createPlan(user.getUserId(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlan);
        } catch (Exception e) {
            log.error("일정 생성 실패: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 일정 수정
    @PutMapping("/{planId}")
    public ResponseEntity<PlanDto> updatePlan(
            @PathVariable Integer planId,
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody CreatePlanRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            PlanDto updatedPlan = planService.updatePlan(planId, user.getUserId(), request);
            return ResponseEntity.ok(updatedPlan);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("일정 수정 실패: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 일정 삭제
    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deletePlan(
            @PathVariable Integer planId,
            @AuthenticationPrincipal CustomOAuth2User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            planService.deletePlan(planId, user.getUserId());
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("일정 삭제 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 일정 아이템 추가
    @PostMapping("/{planId}/day/{dayId}/item")
    public ResponseEntity<PlanItemDto> addPlanItem(
            @PathVariable Integer planId,
            @PathVariable Integer dayId,
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody CreatePlanItemRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        request.setDayId(dayId);

        try {
            PlanItemDto createdItem = planService.addPlanItem(user.getUserId(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("일정 아이템 추가 실패: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 계획 멤버 조회
    @GetMapping("/{planId}/members")
    public ResponseEntity<List<PlanMemberDto>> getPlanMembers(@PathVariable Integer planId) {
        try {
            List<PlanMemberDto> members = planService.getPlanMembers(planId);
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            log.error("멤버 조회 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 멤버 초대
    @PostMapping("/{planId}/members")
    public ResponseEntity<PlanMemberDto> inviteMember(
            @PathVariable Integer planId,
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody InviteMemberRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            PlanMemberDto invitedMember = planService.inviteMember(planId, user.getUserId(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(invitedMember);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("멤버 초대 실패: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 멤버 제거
    @DeleteMapping("/{planId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Integer planId,
            @PathVariable Integer userId,
            @AuthenticationPrincipal CustomOAuth2User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            planService.removeMember(planId, user.getUserId(), userId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("멤버 제거 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 멤버 역할 수정
    @PutMapping("/{planId}/members/{userId}")
    public ResponseEntity<PlanMemberDto> updateMemberRole(
            @PathVariable Integer planId,
            @PathVariable Integer userId,
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody UpdateMemberRoleRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            PlanMemberDto updatedMember = planService.updateMemberRole(planId, user.getUserId(), userId, request);
            return ResponseEntity.ok(updatedMember);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("멤버 역할 수정 실패: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}