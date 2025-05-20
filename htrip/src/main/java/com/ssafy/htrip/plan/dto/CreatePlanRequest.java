package com.ssafy.htrip.plan.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class CreatePlanRequest {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isPublic = true;
}
