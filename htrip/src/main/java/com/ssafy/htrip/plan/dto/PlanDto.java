package com.ssafy.htrip.plan.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class PlanDto {
    private Integer planId;
    private String title;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isPublic;
    private List<PlanDayDto> days;
}
