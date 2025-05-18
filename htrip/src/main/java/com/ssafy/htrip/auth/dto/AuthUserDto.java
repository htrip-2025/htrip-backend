package com.ssafy.htrip.auth.dto;

import com.ssafy.htrip.common.entity.Role;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserDto {
    private Integer userId;
    private String name;
    private Role role;
}
