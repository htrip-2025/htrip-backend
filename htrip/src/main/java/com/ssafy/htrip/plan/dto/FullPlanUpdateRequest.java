package com.ssafy.htrip.plan.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class FullPlanUpdateRequest {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isPublic;
    private List<PlanDayUpdateDto> days;

    @Data
    public static class PlanDayUpdateDto {
        private Integer dayId;
        private Integer dayDate;
        private List<PlanItemUpdateDto> items;
    }

    @Data
    public static class PlanItemUpdateDto {
        private Integer itemId; // 기존 아이템이면 ID가 있음, 새 아이템이면 null
        private Integer placeId;
        private Integer sequence;
        private String startTime;
        private String endTime;
        private String memo;
    }
}