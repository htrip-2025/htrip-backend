package com.ssafy.htrip.plan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePlanRequest {
    private String title;
    private int userId;
}
