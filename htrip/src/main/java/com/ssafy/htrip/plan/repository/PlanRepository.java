package com.ssafy.htrip.plan.repository;

import com.ssafy.htrip.plan.entity.Plan;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Integer> {

    @EntityGraph(attributePaths = {"days", "days.items"})
    Optional<Plan> findWithDaysAndItemsByPlanId(Integer planId);
}
