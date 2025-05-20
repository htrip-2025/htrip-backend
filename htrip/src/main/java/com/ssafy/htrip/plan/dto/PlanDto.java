package com.ssafy.htrip.plan.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class PlanDto {
    private Integer planId;
    private String title;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isPublic;
    private List<PlanDayDto> days;
    private List<PlanMemberDto> members;  // 멤버 정보 추가

    // 현재 사용자의 권한 정보 (프론트엔드에서 UI 제어용)
    private Boolean canEdit;
    private Boolean canDelete;
    private String userRole;
}
