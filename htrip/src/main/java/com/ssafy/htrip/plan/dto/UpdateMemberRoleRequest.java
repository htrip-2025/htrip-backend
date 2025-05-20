package com.ssafy.htrip.plan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMemberRoleRequest {
    private String roleName;
    private String nickname;
}