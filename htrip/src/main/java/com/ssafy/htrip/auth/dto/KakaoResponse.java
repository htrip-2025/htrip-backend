package com.ssafy.htrip.auth.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {
    private final Map<String, Object> attributes;


    public KakaoResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "KAKAO";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getEmail() {
        return ((Map<String, Object>) attributes.get("kakao_account"))
                .get("email")
                .toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getName() {
        // properties 안에 nickname, profile 에도 nickname 이 있으니
        // 원하는 쪽을 골라 쓰시면 됩니다.
        return ((Map<String, Object>) attributes.get("properties"))
                .get("nickname")
                .toString();
    }
}
