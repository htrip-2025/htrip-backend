package com.ssafy.htrip.plan.service;

import com.ssafy.htrip.plan.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlanService {
    PlanDto getPlan(Integer planId);
    PlanDto getPlanWithPermission(Integer planId, Integer userId);
    Page<PlanDto> getMyPlansWithPaging(Integer userId, Pageable pageable);

    List<PlanDto> getAllPlans();
    List<PlanDto> getMyPlans(Integer userId);

    PlanDto createPlan(Integer userId, CreatePlanRequest request);
    PlanDto updatePlan(Integer planId, Integer userId, CreatePlanRequest request);
    PlanItemDto addPlanItem(Integer userId, CreatePlanItemRequest request);
    void deletePlan(Integer planId, Integer userId) throws IllegalAccessException;

    // 멤버 관리 메서드
    PlanMemberDto inviteMember(Integer planId, Integer inviterId, InviteMemberRequest request);
    void removeMember(Integer planId, Integer removerId, Integer targetUserId);
    PlanMemberDto updateMemberRole(Integer planId, Integer updaterId, Integer targetUserId, UpdateMemberRoleRequest request);
    List<PlanMemberDto> getPlanMembers(Integer planId);

    // 권한 체크 메서드
    boolean hasEditPermission(Integer planId, Integer userId);
    boolean hasDeletePermission(Integer planId, Integer userId);
}
