package com.ssafy.htrip.admin.dto;

import com.ssafy.htrip.common.entity.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCheckDto {
    private Integer userId;
    private String name;
    private Role role;
}