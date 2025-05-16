package com.ssafy.htrip.plan.service;

import com.ssafy.htrip.plan.dto.PlanDto;
import com.ssafy.htrip.plan.dto.PlanDayDto;
import com.ssafy.htrip.plan.dto.PlanItemDto;
import com.ssafy.htrip.plan.entity.Plan;
import com.ssafy.htrip.plan.entity.PlanDay;
import com.ssafy.htrip.plan.entity.PlanItem;
import com.ssafy.htrip.plan.repository.PlanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private final PlanRepository planRepository;

    public PlanDto getPlan(Integer planId) {
        Plan plan = planRepository
                .findWithDaysAndItemsByPlanId(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found: " + planId));

        PlanDto dto = new PlanDto();
        dto.setPlanId(plan.getPlanId());
        dto.setTitle(plan.getTitle());
        dto.setCreateDate(plan.getCreateDate());
        dto.setUpdateDate(plan.getUpdateDate());
        dto.setStartDate(plan.getStartDate());
        dto.setEndDate(plan.getEndDate());
        dto.setPublic(plan.isPublic());

        dto.setDays(
                plan.getDays().stream()
                        .map(this::toDayDto)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    @Override
    public List<PlanDto> getAllPlans() {
        return List.of();
    }

    private PlanDayDto toDayDto(PlanDay day) {
        PlanDayDto dd = new PlanDayDto();
        dd.setDayId(day.getDayId());
        dd.setDayDate(day.getDayDate());
        dd.setItems(
                day.getItems().stream()
                        .map(this::toItemDto)
                        .collect(Collectors.toList())
        );
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
}
