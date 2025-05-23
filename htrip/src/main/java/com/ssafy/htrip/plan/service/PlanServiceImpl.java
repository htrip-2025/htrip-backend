package com.ssafy.htrip.plan.service;

import com.ssafy.htrip.attraction.entity.Attraction;
import com.ssafy.htrip.attraction.repository.AttractionRepository;
import com.ssafy.htrip.common.entity.User;
import com.ssafy.htrip.common.repository.UserRepository;
import com.ssafy.htrip.plan.dto.*;
import com.ssafy.htrip.plan.entity.*;
import com.ssafy.htrip.plan.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanServiceImpl implements PlanService {
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final PlanDayRepository planDayRepository;
    private final PlanItemRepository planItemRepository;
    private final AttractionRepository attractionRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final PlanMemberRepository planMemberRepository;

    @Override
    public PlanDto getPlan(Integer planId) {
        Plan plan = planRepository
                .findWithDaysAndItemsByPlanId(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found: " + planId));

        return toPlanDto(plan, null);
    }

    @Override
    public PlanDto getPlanWithPermission(Integer planId, Integer userId) {
        Plan plan = planRepository
                .findWithDaysAndItemsByPlanId(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found: " + planId));

        return toPlanDto(plan, userId);
    }

    @Override
    public Page<PlanDto> getMyPlansWithPaging(Integer userId, Pageable pageable) {
        // PlanMember 엔티티를 기반으로 페이징 조회
        Page<PlanMember> planMemberPage = planMemberRepository.findByUserIdWithPlanPaging(userId, pageable);

        // PlanMember 엔티티를 PlanDto로 변환
        return planMemberPage.map(pm -> toPlanDto(pm.getPlan(), userId));
    }

    @Override
    public List<PlanDto> getAllPlans() {
        return planRepository.findAll().stream()
                .filter(Plan::isPublic)  // 공개 계획만
                .map(plan -> toPlanDto(plan, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<PlanDto> getMyPlans(Integer userId) {
        List<PlanMember> planMembers = planMemberRepository.findByUserIdWithPlan(userId);
        return planMembers.stream()
                .map(pm -> toPlanDto(pm.getPlan(), userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlanDto createPlan(Integer userId, CreatePlanRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        // 새로운 일정 생성
        Plan plan = new Plan();
        plan.setUser(user);
        plan.setTitle(request.getTitle());
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        plan.setUpdateDate(LocalDateTime.now());
        plan.setPublic(request.isPublic());

        Plan savedPlan = planRepository.save(plan);

        // 계획 생성자를 리더로 자동 추가
        MemberRole leaderRole = memberRoleRepository.findByRoleName("LEADER")
                .orElseThrow(() -> new RuntimeException("LEADER role not found"));

        PlanMember planMember = new PlanMember();
        planMember.setId(new PlanMemberId(savedPlan.getPlanId(), userId));
        planMember.setPlan(savedPlan);
        planMember.setUser(user);
        planMember.setMemberRole(leaderRole);
        planMember.setNickname(user.getNickname());

        planMemberRepository.save(planMember);

        // 날짜별 일정 생성
        if (request.getStartDate() != null && request.getEndDate() != null) {
            createPlanDays(savedPlan, request.getStartDate(), request.getEndDate());
        }

        return toPlanDto(savedPlan, userId);
    }

    @Override
    @Transactional
    public PlanDto updatePlan(Integer planId, Integer userId, CreatePlanRequest request) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found: " + planId));

        // 권한 체크 (LEADER 또는 EDITOR)
        if (!hasEditPermission(planId, userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        // 기본 정보 업데이트
        if (request.getTitle() != null) {
            plan.setTitle(request.getTitle());
        }
        plan.setPublic(request.isPublic());

        // 날짜가 변경된 경우
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (!request.getStartDate().equals(plan.getStartDate()) ||
                    !request.getEndDate().equals(plan.getEndDate())) {

                plan.setStartDate(request.getStartDate());
                plan.setEndDate(request.getEndDate());

                plan.getDays().clear();
                createPlanDays(plan, request.getStartDate(), request.getEndDate());
            }
        }

        Plan updatedPlan = planRepository.save(plan);
        return toPlanDto(updatedPlan, userId);
    }


    @Override
    @Transactional
    public void deletePlan(Integer planId, Integer userId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found: " + planId));

        // 권한 체크 (LEADER만 삭제 가능)
        if (!hasDeletePermission(planId, userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        planRepository.delete(plan);
    }

    @Override
    @Transactional
    public PlanItemDto addPlanItem(Integer userId, CreatePlanItemRequest request) {
        PlanDay planDay = planDayRepository.findById(request.getDayId())
                .orElseThrow(() -> new EntityNotFoundException("PlanDay not found: " + request.getDayId()));

        // 권한 확인
        if (!hasEditPermission(planDay.getPlan().getPlanId(), userId)) {
            throw new IllegalArgumentException("편집 권한이 없습니다.");
        }

        Attraction attraction = attractionRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new EntityNotFoundException("Attraction not found: " + request.getPlaceId()));

        PlanItem planItem = new PlanItem();
        planItem.setPlanDay(planDay);
        planItem.setAttraction(attraction);
        planItem.setSequence(request.getSequence());
        planItem.setStartTime(request.getStartTime());
        planItem.setEndTime(request.getEndTime());
        planItem.setMemo(request.getMemo());

        PlanItem savedItem = planItemRepository.save(planItem);
        return toItemDto(savedItem);
    }

    // 멤버 관리 메서드들
    @Override
    @Transactional
    public PlanMemberDto inviteMember(Integer planId, Integer inviterId, InviteMemberRequest request) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found: " + planId));

        // 초대하는 사람이 리더인지 확인
        PlanMember inviter = planMemberRepository.findByPlanIdAndUserIdWithRole(planId, inviterId)
                .orElseThrow(() -> new IllegalArgumentException("Plan member not found"));

        if (!"LEADER".equals(inviter.getMemberRole().getRoleName())) {
            throw new IllegalArgumentException("멤버 초대 권한이 없습니다.");
        }

        // 초대할 사용자 확인
        User inviteeUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + request.getUserId()));

        // 이미 멤버인지 확인
        if (planMemberRepository.findById(new PlanMemberId(planId, request.getUserId())).isPresent()) {
            throw new IllegalArgumentException("이미 계획의 멤버입니다.");
        }

        // 역할 확인
        MemberRole memberRole = memberRoleRepository.findByRoleName(request.getRoleName())
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + request.getRoleName()));

        // 새 멤버 생성
        PlanMember newMember = new PlanMember();
        newMember.setId(new PlanMemberId(planId, request.getUserId()));
        newMember.setPlan(plan);
        newMember.setUser(inviteeUser);
        newMember.setMemberRole(memberRole);
        newMember.setNickname(request.getNickname() != null ? request.getNickname() : inviteeUser.getNickname());

        PlanMember savedMember = planMemberRepository.save(newMember);
        return toPlanMemberDto(savedMember);
    }

    @Override
    @Transactional
    public void removeMember(Integer planId, Integer removerId, Integer targetUserId) {
        // 권한 확인 (리더만 멤버 제거 가능, 단 자신은 제거할 수 없음)
        PlanMember remover = planMemberRepository.findByPlanIdAndUserIdWithRole(planId, removerId)
                .orElseThrow(() -> new IllegalArgumentException("Plan member not found"));

        if (!"LEADER".equals(remover.getMemberRole().getRoleName())) {
            throw new IllegalArgumentException("멤버 제거 권한이 없습니다.");
        }

        if (removerId.equals(targetUserId)) {
            throw new IllegalArgumentException("자기 자신은 제거할 수 없습니다.");
        }

        PlanMemberId memberId = new PlanMemberId(planId, targetUserId);
        PlanMember targetMember = planMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Plan member not found"));

        planMemberRepository.delete(targetMember);
    }

    @Override
    @Transactional
    public PlanMemberDto updateMemberRole(Integer planId, Integer updaterId, Integer targetUserId, UpdateMemberRoleRequest request) {
        // 권한 확인 (리더만 역할 변경 가능)
        PlanMember updater = planMemberRepository.findByPlanIdAndUserIdWithRole(planId, updaterId)
                .orElseThrow(() -> new IllegalArgumentException("Plan member not found"));

        if (!"LEADER".equals(updater.getMemberRole().getRoleName())) {
            throw new IllegalArgumentException("역할 변경 권한이 없습니다.");
        }

        // 대상 멤버 확인
        PlanMember targetMember = planMemberRepository.findByPlanIdAndUserIdWithRole(planId, targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("Plan member not found"));

        // 리더는 자신의 역할을 변경할 수 없음
        if (updaterId.equals(targetUserId) && "LEADER".equals(targetMember.getMemberRole().getRoleName())) {
            throw new IllegalArgumentException("리더는 자신의 역할을 변경할 수 없습니다.");
        }

        // 새로운 역할 확인
        MemberRole newRole = memberRoleRepository.findByRoleName(request.getRoleName())
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + request.getRoleName()));

        // 역할 업데이트
        targetMember.setMemberRole(newRole);
        if (request.getNickname() != null) {
            targetMember.setNickname(request.getNickname());
        }

        PlanMember updatedMember = planMemberRepository.save(targetMember);
        return toPlanMemberDto(updatedMember);
    }

    @Override
    public List<PlanMemberDto> getPlanMembers(Integer planId) {
        List<PlanMember> members = planMemberRepository.findByPlanIdWithUserAndRole(planId);
        return members.stream()
                .map(this::toPlanMemberDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasEditPermission(Integer planId, Integer userId) {
        return planMemberRepository.findByPlanIdAndUserIdWithRole(planId, userId)
                .map(pm -> pm.getMemberRole().getCanEdit())
                .orElse(false);
    }

    @Override
    public boolean hasDeletePermission(Integer planId, Integer userId) {
        return planMemberRepository.findByPlanIdAndUserIdWithRole(planId, userId)
                .map(pm -> pm.getMemberRole().getCanDelete())
                .orElse(false);
    }

    // 유틸리티 메서드들
    private void createPlanDays(Plan plan, LocalDate startDate, LocalDate endDate) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        for (int i = 0; i < daysBetween; i++) {
            PlanDay planDay = new PlanDay();
            planDay.setPlan(plan);
            planDay.setDayDate(i + 1);
            plan.getDays().add(planDay);
        }
    }

    private PlanDto toPlanDto(Plan plan, Integer userId) {
        PlanDto dto = new PlanDto();
        dto.setPlanId(plan.getPlanId());
        dto.setTitle(plan.getTitle());
        dto.setCreateDate(plan.getCreateDate());
        dto.setUpdateDate(plan.getUpdateDate());
        dto.setStartDate(plan.getStartDate());
        dto.setEndDate(plan.getEndDate());
        dto.setPublic(plan.isPublic());

        if (plan.getDays() != null) {
            dto.setDays(
                    plan.getDays().stream()
                            .map(this::toDayDto)
                            .collect(Collectors.toList())
            );
        }

        if (plan.getMembers() != null) {
            dto.setMembers(
                    plan.getMembers().stream()
                            .map(this::toPlanMemberDto)
                            .collect(Collectors.toList())
            );
        }

        // 현재 사용자의 권한 정보 설정
        if (userId != null) {
            planMemberRepository.findByPlanIdAndUserIdWithRole(plan.getPlanId(), userId)
                    .ifPresent(pm -> {
                        dto.setCanEdit(pm.getMemberRole().getCanEdit());
                        dto.setCanDelete(pm.getMemberRole().getCanDelete());
                        dto.setUserRole(pm.getMemberRole().getRoleName());
                    });
        }

        return dto;
    }

    private PlanDayDto toDayDto(PlanDay day) {
        PlanDayDto dd = new PlanDayDto();
        dd.setDayId(day.getDayId());
        dd.setDayDate(day.getDayDate());

        if (day.getItems() != null) {
            dd.setItems(
                    day.getItems().stream()
                            .map(this::toItemDto)
                            .collect(Collectors.toList())
            );
        }
        return dd;
    }

    private PlanItemDto toItemDto(PlanItem item) {
        PlanItemDto id = new PlanItemDto();
        id.setItemId(item.getItemId());
        id.setSequence(item.getSequence());
        id.setStartTime(item.getStartTime());
        id.setEndTime(item.getEndTime());
        id.setMemo(item.getMemo());
        id.setPlaceId(item.getAttraction().getPlaceId());
        return id;
    }

    private PlanMemberDto toPlanMemberDto(PlanMember planMember) {
        PlanMemberDto dto = new PlanMemberDto();
        dto.setPlanId(planMember.getPlan().getPlanId());
        dto.setUserId(planMember.getUser().getUserId());
        dto.setRoleName(planMember.getMemberRole().getRoleName());
        dto.setCanEdit(planMember.getMemberRole().getCanEdit());
        dto.setCanDelete(planMember.getMemberRole().getCanDelete());
        dto.setNickname(planMember.getNickname());

        // 사용자 기본 정보 설정
        User user = planMember.getUser();
        com.ssafy.htrip.common.dto.UserDto userDto = com.ssafy.htrip.common.dto.UserDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImgUrl(user.getProfileImgUrl())
                .build();
        dto.setUser(userDto);

        return dto;
    }
}