package com.ssafy.htrip.plan.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class CreatePlanItemRequest {
    private Integer dayId;
    private Integer placeId;
    private Integer sequence;
    private LocalTime startTime;
    private LocalTime endTime;
    private String memo;
}
