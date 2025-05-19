package com.ssafy.htrip;

import com.ssafy.htrip.auth.util.JWTUtil;
import com.ssafy.htrip.common.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.jwt.secret=testSecretKeyForJWTTesting123456789012345678901234567890"
})
class JWTUtilTest {

    private JWTUtil jwtUtil;
    private final String testSecret = "testSecretKeyForJWTTesting123456789012345678901234567890";

    @BeforeEach
    void setUp() {
        jwtUtil = new JWTUtil(testSecret);
    }

    @Test
    void JWT_토큰_생성_테스트() {
        // given
        Integer userId = 1;
        String name = "테스트유저";
        Role role = Role.USER;
        Long expiredMs = 1000 * 60 * 60L; // 1시간

        // when
        String token = jwtUtil.createJwt(userId, name, role, expiredMs);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void JWT_토큰에서_사용자정보_추출_테스트() {
        // given
        Integer userId = 1;
        String name = "테스트유저";
        Role role = Role.USER;
        Long expiredMs = 1000 * 60 * 60L;

        String token = jwtUtil.createJwt(userId, name, role, expiredMs);

        // when & then
        assertThat(jwtUtil.getUserId(token)).isEqualTo(userId);
        assertThat(jwtUtil.getUserNickname(token)).isEqualTo(name);
        assertThat(jwtUtil.getRole(token)).isEqualTo(role);
    }

    @Test
    void JWT_토큰_만료_검증_테스트() {
        // given
        Integer userId = 1;
        String name = "테스트유저";
        Role role = Role.USER;
        Long expiredMs = 1L; // 1밀리초 (즉시 만료)

        String token = jwtUtil.createJwt(userId, name, role, expiredMs);

        // when & then
        try {
            Thread.sleep(10); // 토큰이 만료되도록 대기
            assertThat(jwtUtil.isExpired(token)).isTrue();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void 유효한_JWT_토큰_만료_검증_테스트() {
        // given
        Integer userId = 1;
        String name = "테스트유저";
        Role role = Role.USER;
        Long expiredMs = 1000 * 60 * 60L; // 1시간

        String token = jwtUtil.createJwt(userId, name, role, expiredMs);

        // when & then
        assertThat(jwtUtil.isExpired(token)).isFalse();
    }
}
