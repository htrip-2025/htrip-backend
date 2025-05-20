package com.ssafy.htrip.plan.repository;

import com.ssafy.htrip.plan.entity.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRoleRepository extends JpaRepository<MemberRole, Integer> {
    Optional<MemberRole> findByRoleName(String roleName);
}