package com.ssafy.htrip.attraction.controller;

import com.ssafy.htrip.attraction.dto.AttractionDto;
import com.ssafy.htrip.attraction.dto.AttractionSearchRequest;
import com.ssafy.htrip.attraction.service.AttractionService;
import com.ssafy.htrip.auth.dto.CustomOAuth2User;
import com.ssafy.htrip.favorite.dto.CreateFavoriteRequest;
import com.ssafy.htrip.favorite.service.FavoriteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
@Tag(name = "Travel", description = "여행지/관광지 관리 API")
public class TravelController {

    private final AttractionService attractionService;
    private final FavoriteService  favoriteService;

    @GetMapping("/{placeId}")
    public ResponseEntity<AttractionDto> findById(@PathVariable Integer placeId) throws Throwable {
        AttractionDto dto = attractionService.findById(placeId);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/")
    public ResponseEntity<List<AttractionDto>> previewRandom(@RequestParam(defaultValue = "6") int n) {
        List<AttractionDto> dto = attractionService.findRandom(n);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AttractionDto>> searchAttractions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer areaCode,
            @RequestParam(required = false) Integer sigunguCode,
            @PageableDefault(size = 20, sort = "placeId") Pageable pageable) {

        AttractionSearchRequest request = new AttractionSearchRequest(keyword, areaCode, sigunguCode);
        Page<AttractionDto> result = attractionService.searchAttractions(request, pageable);

        return ResponseEntity.ok(result);
    }

    // 여행지 찜하기
    @PostMapping("/{placeId}/favorite")
    public ResponseEntity<Map<String, String>> addToFavorite(
            @PathVariable Integer placeId,
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody(required = false) CreateFavoriteRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            // request가 null인 경우 기본 요청 생성
            if (request == null) {
                request = new CreateFavoriteRequest();
            } else {
                request.setPlaceId(placeId);
            }

            favoriteService.addFavorite(user.getUserId(), request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "찜 목록에 추가되었습니다."));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "이미 찜한 여행지입니다."));
        }
    }

    // 여행지 찜 해제
    @DeleteMapping("/{placeId}/favorite")
    public ResponseEntity<Map<String, String>> removeFromFavorite(
            @PathVariable Integer placeId,
            @AuthenticationPrincipal CustomOAuth2User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            favoriteService.removeFavoriteByUserAndPlace(user.getUserId(), placeId);
            return ResponseEntity.ok(Map.of("message", "찜 목록에서 제거되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "찜하지 않은 여행지입니다."));
        }
    }

    // 특정 여행지의 찜 개수 조회
    @GetMapping("/{placeId}/favorite/count")
    public ResponseEntity<Map<String, Long>> getFavoriteCount(@PathVariable Integer placeId) {
        Long count = favoriteService.getFavoriteCount(placeId);
        return ResponseEntity.ok(Map.of("count", count));
    }

}
