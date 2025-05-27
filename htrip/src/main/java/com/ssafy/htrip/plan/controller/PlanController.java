package com.ssafy.htrip.plan.controller;

import com.ssafy.htrip.auth.dto.CustomOAuth2User;
import com.ssafy.htrip.plan.dto.*;
import com.ssafy.htrip.plan.service.PlanService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
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
    @GetMapping()
    public ResponseEntity<List<PlanDto>> getAllPlans() {
        List<PlanDto> list = planService.getAllPlans();
        return ResponseEntity.ok(list);
    }

    // 내가 참여한 계획 목록 조회
    @GetMapping("/my")
    public ResponseEntity<Page<PlanDto>> getMyPlans(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updateDate") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size);

        Page<PlanDto> myPlans = planService.getMyPlansWithPaging(user.getUserId(), pageable);
        return ResponseEntity.ok(myPlans);
    }

    // 일정 생성
    @PostMapping()
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

    // 계획 전체 업데이트 (계획 정보 + 모든 아이템)
    @PutMapping("/{planId}/full")
    public ResponseEntity<PlanDto> updateFullPlan(
            @PathVariable Integer planId,
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody FullPlanUpdateRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            PlanDto updatedPlan = planService.updateFullPlan(planId, user.getUserId(), request);
            return ResponseEntity.ok(updatedPlan);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("전체 계획 수정 실패: ", e);
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

    // 일정 항목 삭제
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Void> deletePlanItem(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Integer itemId) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            planService.deletePlanItem(user.getUserId(), itemId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("일정 항목 삭제 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 일정 항목 수정
    @PutMapping("/item/{itemId}")
    public ResponseEntity<PlanItemDto> updatePlanItem(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Integer itemId,
            @RequestBody CreatePlanItemRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            PlanItemDto updatedItem = planService.updatePlanItem(user.getUserId(), itemId, request);
            return ResponseEntity.ok(updatedItem);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("일정 항목 수정 실패: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 일정 항목 순서 일괄 변경
    @PutMapping("/day/{dayId}/items/sequence")
    public ResponseEntity<List<PlanItemDto>> updateItemSequence(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Integer dayId,
            @RequestBody List<PlanItemSequenceRequest> requests) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<PlanItemDto> updatedItems = planService.updateItemSequence(user.getUserId(), dayId, requests);
            return ResponseEntity.ok(updatedItems);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("일정 항목 순서 변경 실패: ", e);
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