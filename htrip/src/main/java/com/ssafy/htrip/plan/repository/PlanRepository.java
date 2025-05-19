package com.ssafy.htrip.plan.repository;

import com.ssafy.htrip.plan.entity.Plan;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Integer> {

    @EntityGraph(attributePaths = {"days", "days.items"})
    Optional<Plan> findWithDaysAndItemsByPlanId(Integer planId);

    Long countByUserUserId(Integer userId);
    // 완료된 여행 계획 수 (endDate가 현재 날짜 이전)
    @Query("SELECT COUNT(p) FROM Plan p WHERE p.user.userId = :userId AND p.endDate < CURRENT_DATE")
    Long countCompletedPlansByUserId(@Param("userId") Integer userId);

    // 사용자의 모든 계획 조회 (최신 순)
    @Query("SELECT p FROM Plan p WHERE p.user.userId = :userId ORDER BY p.createDate DESC")
    java.util.List<Plan> findByUserUserIdOrderByCreateDateDesc(@Param("userId") Integer userId);
}
