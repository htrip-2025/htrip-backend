package com.ssafy.htrip.plan.repository;

import com.ssafy.htrip.plan.entity.PlanMember;
import com.ssafy.htrip.plan.entity.PlanMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlanMemberRepository extends JpaRepository<PlanMember, PlanMemberId> {

    // 특정 계획의 모든 멤버 조회
    @Query("SELECT pm FROM PlanMember pm JOIN FETCH pm.user JOIN FETCH pm.memberRole WHERE pm.plan.planId = :planId")
    List<PlanMember> findByPlanIdWithUserAndRole(@Param("planId") Integer planId);

    // 특정 사용자가 참여한 모든 계획 조회
    @Query("SELECT pm FROM PlanMember pm JOIN FETCH pm.plan WHERE pm.user.userId = :userId")
    List<PlanMember> findByUserIdWithPlan(@Param("userId") Integer userId);

    // 특정 사용자의 특정 계획에서의 역할 조회
    @Query("SELECT pm FROM PlanMember pm JOIN FETCH pm.memberRole WHERE pm.plan.planId = :planId AND pm.user.userId = :userId")
    Optional<PlanMember> findByPlanIdAndUserIdWithRole(@Param("planId") Integer planId, @Param("userId") Integer userId);

    // 특정 계획의 리더 조회
    @Query("SELECT pm FROM PlanMember pm JOIN FETCH pm.user WHERE pm.plan.planId = :planId AND pm.memberRole.roleName = 'LEADER'")
    Optional<PlanMember> findLeaderByPlanId(@Param("planId") Integer planId);
}