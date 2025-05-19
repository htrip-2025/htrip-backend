package com.ssafy.htrip.member.controller;

import com.ssafy.htrip.auth.dto.CustomOAuth2User;
import com.ssafy.htrip.favorite.dto.FavoriteDto;
import com.ssafy.htrip.favorite.dto.UpdateFavoriteRequest;
import com.ssafy.htrip.favorite.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final FavoriteService favoriteService;

    // 내 찜 목록 조회
    @GetMapping("/favorite")
    public ResponseEntity<List<FavoriteDto>> getMyFavorites(
            @AuthenticationPrincipal CustomOAuth2User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<FavoriteDto> favorites = favoriteService.getUserFavorites(user.getUserId());
        return ResponseEntity.ok(favorites);
    }

    // 태그별 찜 검색
    @GetMapping("/favorite/search")
    public ResponseEntity<List<FavoriteDto>> searchFavoritesByTag(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestParam String tag) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<FavoriteDto> favorites = favoriteService.searchFavoritesByTag(user.getUserId(), tag);
        return ResponseEntity.ok(favorites);
    }

    // 찜 정보 수정 (메모, 태그)
    @PutMapping("/favorite/{favoriteNo}")
    public ResponseEntity<FavoriteDto> updateFavorite(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Integer favoriteNo,
            @RequestBody UpdateFavoriteRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            FavoriteDto updated = favoriteService.updateFavorite(user.getUserId(), favoriteNo, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // 찜 제거 (멤버 페이지에서)
    @DeleteMapping("/favorite/{favoriteNo}")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Integer favoriteNo) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            favoriteService.removeFavorite(user.getUserId(), favoriteNo);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}