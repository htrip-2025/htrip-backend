package com.ssafy.htrip.member.controller;

import com.ssafy.htrip.auth.dto.CustomOAuth2User;
import com.ssafy.htrip.favorite.dto.FavoriteDto;
import com.ssafy.htrip.favorite.dto.UpdateFavoriteRequest;
import com.ssafy.htrip.favorite.service.FavoriteService;
import com.ssafy.htrip.member.dto.MemberProfileDto;
import com.ssafy.htrip.member.dto.MemberStatsDto;
import com.ssafy.htrip.member.dto.UpdateProfileRequest;
import com.ssafy.htrip.member.service.MemberService;
import com.ssafy.htrip.plan.service.PlanService;
import com.ssafy.htrip.review.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 프로필 및 개인정보 관리 API")
public class MemberController {

    private final FavoriteService favoriteService;
    private final MemberService memberService;
    private final PlanService planService;
    private final ReviewService reviewService;
    //private final BoardService boardService;

    // === 프로필 관리 ===

    // 내 프로필 조회
    @GetMapping()
    public ResponseEntity<MemberProfileDto> getMyProfile(
            @AuthenticationPrincipal CustomOAuth2User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        MemberProfileDto profile = memberService.getProfile(user.getUserId());
        return ResponseEntity.ok(profile);
    }

    // 프로필 수정
    @PutMapping()
    public ResponseEntity<MemberProfileDto> updateProfile(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody UpdateProfileRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        MemberProfileDto updated = memberService.updateProfile(user.getUserId(), request);
        return ResponseEntity.ok(updated);
    }

    // 마이페이지 통계
    @GetMapping("/stats")
    public ResponseEntity<MemberStatsDto> getMyStats(
            @AuthenticationPrincipal CustomOAuth2User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        MemberStatsDto stats = memberService.getStats(user.getUserId());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @AuthenticationPrincipal CustomOAuth2User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer userId = user.getUserId();
        Map<String, Object> dashboard = new HashMap<>();

        try {
            // 프로필 및 통계 정보
            dashboard.put("profile", memberService.getProfile(userId));
            dashboard.put("stats", memberService.getStats(userId));

            // 최근 여행 계획 - Sort 필드를 "plan.updateDate"로 변경
            Pageable planPageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "plan.updateDate"));
            dashboard.put("recentPlans", planService.getMyPlansWithPaging(userId, planPageable));

            // 최근 찜한 여행지
            Pageable favoritePageable = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "createAt"));
            dashboard.put("recentFavorites", favoriteService.getUserFavoritesWithPaging(userId, favoritePageable));

            // 최근 리뷰
            Pageable reviewPageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createDate"));
            dashboard.put("recentReviews", reviewService.getReviewsByUserId(userId, reviewPageable));


//        // 최근 게시글
//        Pageable boardPageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "writeDate"));
//        dashboard.put("recentPosts", boardService.getMyPosts(userId, boardPageable));
//
//        // 최근 댓글
//        Pageable commentPageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "writeDate"));
//        dashboard.put("recentComments", commentService.getMyComments(userId, commentPageable));
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("대시보드 조회 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "대시보드 정보 조회에 실패했습니다."));
        }
    }

    // 내 찜 목록 조회
    @GetMapping("/favorite")
    public ResponseEntity<Page<FavoriteDto>> getMyFavorites(
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<FavoriteDto> favorites = favoriteService.getUserFavoritesWithPaging(user.getUserId(), pageable);
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