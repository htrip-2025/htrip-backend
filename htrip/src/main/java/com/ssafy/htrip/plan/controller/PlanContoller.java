package com.ssafy.htrip.plan.controller;

import com.ssafy.htrip.attraction.entity.Attraction;
import com.ssafy.htrip.plan.dto.CreatePlanRequest;
import com.ssafy.htrip.plan.dto.PlanDto;
import com.ssafy.htrip.plan.entity.Plan;
import com.ssafy.htrip.plan.entity.PlanDay;
import com.ssafy.htrip.plan.repository.PlanRepository;
import com.ssafy.htrip.plan.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
public class PlanContoller {

    private final PlanService planService;
    // 단건 조회
    @GetMapping("/{planId}")
    public ResponseEntity<PlanDto> getPlan(@PathVariable Integer planId) {
        PlanDto dto = planService.getPlan(planId);
        return ResponseEntity.ok(dto);
    }

    // 전체 목록 조회
    @GetMapping
    public ResponseEntity<List<PlanDto>> getAllPlans() {
        // 필요하다면 service 에서 findAll + 매핑하는 메서드 추가
        List<PlanDto> list = planService.getAllPlans();
        return ResponseEntity.ok(list);
    }

//    @PostMapping("/")
//    public ResponseEntity<Plan> createPlan(@RequestBody CreatePlanRequest request) throws NotFoundException {
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(newPlan);
//    }
//
//    @PatchMapping("/{planId}")
//    public ResponseEntity<Plan> update(@PathVariable int planId) throws NotFoundException {
//        return ResponseEntity.ok(planId);
//    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> delete(@PathVariable int planId) throws NotFoundException {
        return ResponseEntity.noContent().build();
    }

}
