package com.ssafy.htrip.plan.service;

import com.ssafy.htrip.plan.dto.PlanDto;

import java.util.List;

public interface PlanService {
    public PlanDto getPlan(Integer planId);
    List<PlanDto> getAllPlans();
}
