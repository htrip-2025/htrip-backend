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
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    public PlanDto updateFullPlan(Integer planId, Integer userId, FullPlanUpdateRequest request) {
        // 1. 계획 찾기
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found: " + planId));

        // 2. 권한 확인
        if (!hasEditPermission(planId, userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        // 3. 기본 정보 업데이트
        if (request.getTitle() != null) {
            plan.setTitle(request.getTitle());
        }
        plan.setPublic(request.isPublic());

        // 4. 날짜 정보 업데이트
        if (request.getStartDate() != null && request.getEndDate() != null) {
            plan.setStartDate(request.getStartDate());
            plan.setEndDate(request.getEndDate());
        }

        // 5. 기존 계획 일자 Map 생성 (dayDate -> PlanDay)
        Map<Integer, PlanDay> existingDaysMap = plan.getDays().stream()
                .collect(Collectors.toMap(PlanDay::getDayDate, day -> day));

        // 6. 새 계획 일자 처리
        if (request.getDays() != null && !request.getDays().isEmpty()) {
            // 이전 계획 일자 참조 저장 (나중에 삭제할 항목 확인용)
            Set<PlanDay> oldDays = new HashSet<>(plan.getDays());
            plan.getDays().clear();

            for (FullPlanUpdateRequest.PlanDayUpdateDto dayDto : request.getDays()) {
                PlanDay day;

                // 기존 일자가 있으면 재사용, 없으면 새로 생성
                if (existingDaysMap.containsKey(dayDto.getDayDate())) {
                    day = existingDaysMap.get(dayDto.getDayDate());
                    day.getItems().clear(); // 기존 아이템 모두 제거
                    oldDays.remove(day); // 삭제 목록에서 제외
                } else {
                    day = new PlanDay();
                    day.setPlan(plan);
                    day.setDayDate(dayDto.getDayDate());
                }

                // 아이템 처리
                if (dayDto.getItems() != null) {
                    for (int i = 0; i < dayDto.getItems().size(); i++) {
                        FullPlanUpdateRequest.PlanItemUpdateDto itemDto = dayDto.getItems().get(i);

                        PlanItem item;
                        if (itemDto.getItemId() != null) {
                            // 기존 아이템 찾기
                            item = planItemRepository.findById(itemDto.getItemId())
                                    .orElse(null);

                            // 다른 계획의 아이템이면 무시
                            if (item != null && !item.getPlanDay().getPlan().getPlanId().equals(planId)) {
                                item = null;
                            }
                        } else {
                            item = null;
                        }

                        // 아이템이 없으면 새로 생성
                        if (item == null) {
                            item = new PlanItem();

                            // 새 장소 정보 설정
                            if (itemDto.getPlaceId() != null) {
                                Attraction attraction = attractionRepository.findById(itemDto.getPlaceId())
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                "Attraction not found: " + itemDto.getPlaceId()));
                                item.setAttraction(attraction);
                            } else {
                                // placeId는 필수
                                continue;
                            }
                        }

                        // 순서는 front에서 받은 순서대로
                        item.setSequence(i + 1);

                        // 나머지 정보 업데이트
                        if (itemDto.getStartTime() != null && !itemDto.getStartTime().isEmpty()) {
                            item.setStartTime(LocalTime.parse(itemDto.getStartTime()));
                        }

                        if (itemDto.getEndTime() != null && !itemDto.getEndTime().isEmpty()) {
                            item.setEndTime(LocalTime.parse(itemDto.getEndTime()));
                        }

                        item.setMemo(itemDto.getMemo());
                        item.setPlanDay(day);

                        day.getItems().add(item);
                    }
                }

                plan.getDays().add(day);
            }

            // 삭제된 일자의 아이템들 모두 제거 (CASCADE 설정이 있더라도 명시적으로)
            for (PlanDay oldDay : oldDays) {
                for (PlanItem item : oldDay.getItems()) {
                    planItemRepository.delete(item);
                }
                planDayRepository.delete(oldDay);
            }
        }

        plan.setUpdateDate(LocalDateTime.now());
        Plan updatedPlan = planRepository.save(plan);

        return toPlanDto(updatedPlan, userId);
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

    @Override
    @Transactional
    public void deletePlanItem(Integer userId, Integer itemId) {
        PlanItem planItem = planItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("PlanItem not found: " + itemId));

        Integer planId = planItem.getPlanDay().getPlan().getPlanId();

        // 권한 확인
        if (!hasEditPermission(planId, userId)) {
            throw new IllegalArgumentException("편집 권한이 없습니다.");
        }

        planItemRepository.delete(planItem);
    }

    @Override
    @Transactional
    public PlanItemDto updatePlanItem(Integer userId, Integer itemId, CreatePlanItemRequest request) {
        PlanItem planItem = planItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("PlanItem not found: " + itemId));

        Integer planId = planItem.getPlanDay().getPlan().getPlanId();

        // 권한 확인
        if (!hasEditPermission(planId, userId)) {
            throw new IllegalArgumentException("편집 권한이 없습니다.");
        }

        // 항목이 다른 일자로 이동하는 경우
        if (request.getDayId() != null && !request.getDayId().equals(planItem.getPlanDay().getDayId())) {
            PlanDay newDay = planDayRepository.findById(request.getDayId())
                    .orElseThrow(() -> new EntityNotFoundException("PlanDay not found: " + request.getDayId()));

            // 같은 여행 계획 내의 일자인지 확인
            if (!newDay.getPlan().getPlanId().equals(planId)) {
                throw new IllegalArgumentException("다른 여행 계획의 일자로 이동할 수 없습니다.");
            }

            planItem.setPlanDay(newDay);
        }

        // 방문 장소가 변경되는 경우
        if (request.getPlaceId() != null && !request.getPlaceId().equals(planItem.getAttraction().getPlaceId())) {
            Attraction newAttraction = attractionRepository.findById(request.getPlaceId())
                    .orElseThrow(() -> new EntityNotFoundException("Attraction not found: " + request.getPlaceId()));

            planItem.setAttraction(newAttraction);
        }

        // 기타 정보 업데이트
        if (request.getSequence() != null) {
            planItem.setSequence(request.getSequence());
        }

        if (request.getStartTime() != null) {
            planItem.setStartTime(request.getStartTime());
        }

        if (request.getEndTime() != null) {
            planItem.setEndTime(request.getEndTime());
        }

        if (request.getMemo() != null) {
            planItem.setMemo(request.getMemo());
        }

        PlanItem updatedItem = planItemRepository.save(planItem);
        return toItemDto(updatedItem);
    }

    @Override
    @Transactional
    public List<PlanItemDto> updateItemSequence(Integer userId, Integer dayId, List<PlanItemSequenceRequest> requests) {
        PlanDay planDay = planDayRepository.findById(dayId)
                .orElseThrow(() -> new EntityNotFoundException("PlanDay not found: " + dayId));

        Integer planId = planDay.getPlan().getPlanId();

        // 권한 확인
        if (!hasEditPermission(planId, userId)) {
            throw new IllegalArgumentException("편집 권한이 없습니다.");
        }

        // 요청된 모든 항목이 해당 일자에 속하는지 확인
        List<Integer> requestItemIds = requests.stream()
                .map(PlanItemSequenceRequest::getItemId)
                .collect(Collectors.toList());

        List<PlanItem> items = planItemRepository.findAllById(requestItemIds);

        // 모든 항목이 같은 일자에 속하는지 확인
        for (PlanItem item : items) {
            if (!item.getPlanDay().getDayId().equals(dayId)) {
                throw new IllegalArgumentException("다른 일자의 항목 순서는 변경할 수 없습니다.");
            }
        }

        // 순서 업데이트
        Map<Integer, Integer> sequenceMap = requests.stream()
                .collect(Collectors.toMap(
                        PlanItemSequenceRequest::getItemId,
                        PlanItemSequenceRequest::getSequence
                ));

        for (PlanItem item : items) {
            Integer newSequence = sequenceMap.get(item.getItemId());
            if (newSequence != null) {
                item.setSequence(newSequence);
            }
        }

        List<PlanItem> updatedItems = planItemRepository.saveAll(items);
        return updatedItems.stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
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
            PlanDay savedDay = planDayRepository.save(planDay);
            plan.getDays().add(savedDay);
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