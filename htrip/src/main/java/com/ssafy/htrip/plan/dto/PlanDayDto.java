package com.ssafy.htrip.plan.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class PlanDayDto {
    private Integer dayId;
    private Integer dayDate;
    private List<PlanItemDto> items;
}
