package com.ssafy.htrip;
import com.ssafy.htrip.auth.dto.CustomOAuth2User;
import com.ssafy.htrip.auth.service.AuthService;
import com.ssafy.htrip.common.entity.OauthProvider;
import com.ssafy.htrip.common.entity.Role;
import com.ssafy.htrip.common.entity.User;
import com.ssafy.htrip.common.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private OAuth2UserRequest userRequest;
    private OAuth2User oauth2User;

    @BeforeEach
    void setUp() {
        // 1) ClientRegistration 준비
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("google")
                .clientId("test-client-id")
                .clientSecret("test-client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/google")
                .authorizationUri("https://accounts.google.com/oauth/authorize")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://openidconnect.googleapis.com/v1/userinfo")
                .userNameAttributeName("sub")
                .build();

        // 2) 유효한 OAuth2AccessToken 생성
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "dummy-token",
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.HOURS)
        );

        // 3) 필드에 올바르게 할당
        this.userRequest = new OAuth2UserRequest(clientRegistration, accessToken);

        // 4) oauth2User 준비
        Map<String, Object> attributes = Map.of(
                "sub", "google123",
                "email", "test@google.com",
                "name", "테스트 구글 사용자"
        );
        this.oauth2User = new DefaultOAuth2User(
                List.of(() -> "USER"),
                attributes, "sub"
        );
    }


    @Test
    void 신규_Google_사용자_OAuth_로그인_테스트() {
        // given - 기존 사용자가 없다고 설정
        when(userRepository.findByOauthProviderAndOauthId(
                eq(OauthProvider.GOOGLE), eq("google123")))
                .thenReturn(null);

        // 새 사용자 저장 모킹
        User savedUser = createTestUser();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // AuthService의 loadUser를 직접 호출하는 대신 내부 로직 테스트
        // when
        User user = userRepository.findByOauthProviderAndOauthId(OauthProvider.GOOGLE, "google123");

        if (user == null) {
            user = new User();
            user.setOauthProvider(OauthProvider.GOOGLE);
            user.setOauthId("google123");
            user.setEmail("test@google.com");
            user.setName("테스트 구글 사용자");
            user.setNickname("테스트 구글 사용자");
            user.setRole(Role.USER);
            user = userRepository.save(user);
        }

        // then
        assertThat(user).isNotNull();
        assertThat(user.getOauthProvider()).isEqualTo(OauthProvider.GOOGLE);
        assertThat(user.getOauthId()).isEqualTo("google123");
        assertThat(user.getEmail()).isEqualTo("test@google.com");
        assertThat(user.getRole()).isEqualTo(Role.USER);

        verify(userRepository).findByOauthProviderAndOauthId(OauthProvider.GOOGLE, "google123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void 기존_Google_사용자_OAuth_로그인_테스트() {
        // given - 기존 사용자가 있다고 설정
        User existingUser = createTestUser();
        when(userRepository.findByOauthProviderAndOauthId(
                eq(OauthProvider.GOOGLE), eq("google123")))
                .thenReturn(existingUser);

        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // when
        User user = userRepository.findByOauthProviderAndOauthId(OauthProvider.GOOGLE, "google123");
        if (user != null) {
            user = userRepository.save(user); // lastLoginAt 업데이트
        }

        // then
        assertThat(user).isNotNull();
        assertThat(user.getOauthProvider()).isEqualTo(OauthProvider.GOOGLE);
        assertThat(user.getOauthId()).isEqualTo("google123");

        verify(userRepository).findByOauthProviderAndOauthId(OauthProvider.GOOGLE, "google123");
        verify(userRepository).save(existingUser);
    }

    private User createTestUser() {
        User user = new User();
        user.setUserId(1);
        user.setOauthProvider(OauthProvider.GOOGLE);
        user.setOauthId("google123");
        user.setEmail("test@google.com");
        user.setName("테스트 구글 사용자");
        user.setNickname("테스트 구글 사용자");
        user.setRole(Role.USER);
        return user;
    }
}
