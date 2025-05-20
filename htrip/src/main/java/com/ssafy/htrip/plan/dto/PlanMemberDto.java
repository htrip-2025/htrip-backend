package com.ssafy.htrip.plan.dto;

import com.ssafy.htrip.common.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanMemberDto {
    private Integer planId;
    private Integer userId;
    private String roleName;
    private Boolean canEdit;
    private Boolean canDelete;
    private String nickname;
    private UserDto user;  // 사용자 기본 정보
}
