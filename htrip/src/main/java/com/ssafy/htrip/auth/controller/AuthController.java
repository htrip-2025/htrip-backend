package com.ssafy.htrip.auth.controller;

import com.ssafy.htrip.auth.dto.CustomOAuth2User;
import com.ssafy.htrip.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    MemberService memberService;

    @DeleteMapping("/signout")
    public ResponseEntity<Map<String, String>> deleteAccount(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestParam(required = false) String confirmPassword) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            memberService.deleteAccount(user.getUserId());
            return ResponseEntity.ok(Map.of("message", "계정이 삭제되었습니다."));
        } catch (Exception e) {
            log.error("계정 삭제 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "계정 삭제에 실패했습니다."));
        }
    }
}
