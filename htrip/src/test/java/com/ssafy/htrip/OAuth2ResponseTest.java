package com.ssafy.htrip;

import com.ssafy.htrip.auth.dto.GoogleResponse;
import com.ssafy.htrip.auth.dto.KakaoResponse;
import com.ssafy.htrip.auth.dto.NaverResponse;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class OAuth2ResponseTest {

    @Test
    void Google_Response_테스트() {
        // given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "google123456");
        attributes.put("email", "test@gmail.com");
        attributes.put("name", "구글 테스트 사용자");

        // when
        GoogleResponse response = new GoogleResponse(attributes);

        // then
        assertThat(response.getProvider()).isEqualTo("GOOGLE");
        assertThat(response.getProviderId()).isEqualTo("google123456");
        assertThat(response.getEmail()).isEqualTo("test@gmail.com");
        assertThat(response.getName()).isEqualTo("구글 테스트 사용자");
    }

    @Test
    void Naver_Response_테스트() {
        // given
        Map<String, Object> naverResponse = new HashMap<>();
        naverResponse.put("id", "naver123456");
        naverResponse.put("email", "test@naver.com");
        naverResponse.put("name", "네이버 테스트 사용자");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("response", naverResponse);

        // when
        NaverResponse response = new NaverResponse(attributes);

        // then
        assertThat(response.getProvider()).isEqualTo("NAVER");
        assertThat(response.getProviderId()).isEqualTo("naver123456");
        assertThat(response.getEmail()).isEqualTo("test@naver.com");
        assertThat(response.getName()).isEqualTo("네이버 테스트 사용자");
    }

    @Test
    void Kakao_Response_테스트() {
        // given
        Map<String, Object> kakaoAccount = new HashMap<>();
        kakaoAccount.put("email", "test@kakao.com");

        Map<String, Object> properties = new HashMap<>();
        properties.put("nickname", "카카오 테스트 사용자");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", "kakao123456");
        attributes.put("kakao_account", kakaoAccount);
        attributes.put("properties", properties);

        // when
        KakaoResponse response = new KakaoResponse(attributes);

        // then
        assertThat(response.getProvider()).isEqualTo("KAKAO");
        assertThat(response.getProviderId()).isEqualTo("kakao123456");
        assertThat(response.getEmail()).isEqualTo("test@kakao.com");
        assertThat(response.getName()).isEqualTo("카카오 테스트 사용자");
    }
}
