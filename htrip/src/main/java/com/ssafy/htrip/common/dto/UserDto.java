package com.ssafy.htrip.common.dto;

import com.ssafy.htrip.common.entity.OauthProvider;
import com.ssafy.htrip.common.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Integer userId;
    private String email;
    private OauthProvider oauthProvider;
    private String oauthId;
    private String name;
    private String nickname;
    private String profileImgUrl;
    private LocalDateTime registDate;
    private LocalDateTime lastLoginAt;
    private Role role;
}
