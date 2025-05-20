package com.ssafy.htrip.plan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InviteMemberRequest {
    private Integer userId;
    private String roleName = "VIEWER";  // 기본값: VIEWER
    private String nickname;
}