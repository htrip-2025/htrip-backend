package com.ssafy.htrip.admin.dto;

import com.ssafy.htrip.common.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberRequest {
    private String nickname;         // 닉네임 수정
    private Role role;              // 역할 변경 (USER ↔ ADMIN)
    private Boolean isActive;       // 계정 활성화/비활성화
    //private String memo;            // 관리자 메모 (향후 확장)
}