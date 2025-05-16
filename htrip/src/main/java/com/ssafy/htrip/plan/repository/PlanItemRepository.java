package com.ssafy.htrip.plan.repository;

import com.ssafy.htrip.plan.entity.PlanItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanItemRepository extends JpaRepository<PlanItem, Integer> {
}
