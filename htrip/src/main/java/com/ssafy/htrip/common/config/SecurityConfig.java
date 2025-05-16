package com.ssafy.htrip.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http  // API만 사용한다면 CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        // Swagger 관련 경로는 인증 없이 접근 허용
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()
                        // 그 외 모든 요청은 로그인 필요
                        .anyRequest().authenticated()
                )
                // 나머지 로그인폼은 기본 설정 유지
                .formLogin(Customizer.withDefaults());
        return http.build();
    }
}

