package com.ssafy.htrip.plan.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter @Setter
public class PlanItemDto {
    private Integer itemId;
    private Integer sequence;
    private LocalTime startTime;
    private LocalTime endTime;
    private String memo;
    private Integer placeId;
}
