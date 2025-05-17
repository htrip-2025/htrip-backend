package com.ssafy.htrip.auth.service;

import com.ssafy.htrip.auth.dto.*;
import com.ssafy.htrip.common.entity.Role;
import com.ssafy.htrip.common.entity.User;
import com.ssafy.htrip.common.repository.UserRepository;
import com.ssafy.htrip.common.entity.OauthProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j // Lombok 로거 추가
@Transactional // 전체 메서드에 트랜잭션 적용
public class AuthService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 개인정보가 포함될 수 있으므로 디버그 레벨에서만 로깅
        log.debug("OAuth authentication in progress for provider: {}",
                userRequest.getClientRegistration().getRegistrationId());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = createOAuth2Response(registrationId, oAuth2User);

        try {
            OauthProvider provider = OauthProvider.valueOf(oAuth2Response.getProvider());
            String providerId = oAuth2Response.getProviderId();

            User user = userRepository.findByOauthProviderAndOauthId(provider, providerId);

            if (user == null) {
                user = createNewUser(oAuth2Response, provider, providerId);
            }

            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            return new CustomOAuth2User(convertToAuthUserDto(user));
        } catch (IllegalArgumentException e) {
            log.error("Invalid OAuth provider: {}", oAuth2Response.getProvider(), e);
            throw new OAuth2AuthenticationException("Invalid OAuth provider");
        }
    }

    private OAuth2Response createOAuth2Response(String registrationId, OAuth2User oAuth2User) {
        return switch (registrationId.toLowerCase()) {
            case "naver" -> new NaverResponse(oAuth2User.getAttributes());
            case "google" -> new GoogleResponse(oAuth2User.getAttributes());
            case "kakao" -> new KakaoResponse(oAuth2User.getAttributes());
            default -> throw new OAuth2AuthenticationException("Unsupported OAuth provider: " + registrationId);
        };
    }

    private User createNewUser(OAuth2Response response, OauthProvider provider, String providerId) {
        User user = new User();
        user.setOauthProvider(provider);
        user.setOauthId(providerId);
        user.setName(response.getName());
        user.setNickname(response.getName());
        user.setEmail(response.getEmail());
        user.setRole(Role.USER); // enum 상수 직접 사용

        return userRepository.save(user);
    }

    private AuthUserDto convertToAuthUserDto(User user) {
        AuthUserDto userDto = new AuthUserDto();
        userDto.setUserId(user.getUserId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getNickname());
        userDto.setProfileImgUrl(user.getProfileImgUrl());
        userDto.setRole(user.getRole());
        return userDto;
    }
}
