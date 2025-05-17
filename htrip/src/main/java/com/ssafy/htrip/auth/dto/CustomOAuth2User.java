package com.ssafy.htrip.auth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final AuthUserDto userDTO;

    public CustomOAuth2User(AuthUserDto userDTO) {

        this.userDTO = userDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {

        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return String.valueOf(userDTO.getRole());
            }
        });

        return collection;
    }

    @Override
    public String getName() {

        return userDTO.getName();
    }
    // 편의 메서드: DTO 에 담긴 정보 꺼내기
    public Integer getUserId() {
        return userDTO.getUserId();
    }

    public String getEmail() {
        return userDTO.getEmail();
    }

    public String getNickname() {
        return userDTO.getName();   // 또는 getNickname()
    }

    public String getProfileImgUrl() {
        return userDTO.getProfileImgUrl();
    }
}
