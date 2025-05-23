package com.ssafy.htrip.favorite.service;

import com.ssafy.htrip.favorite.dto.FavoriteDto;
import com.ssafy.htrip.favorite.dto.CreateFavoriteRequest;
import com.ssafy.htrip.favorite.dto.UpdateFavoriteRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FavoriteService {

    // 찜 추가
    FavoriteDto addFavorite(Integer userId, CreateFavoriteRequest request);

    // 찜 제거 (찜 번호로)
    void removeFavorite(Integer userId, Integer favoriteNo);

    // 찜 제거 (사용자ID + 장소ID로) - 여행지 상세에서 사용
    void removeFavoriteByUserAndPlace(Integer userId, Integer placeId);

    // 특정 사용자의 찜 목록 조회
    List<FavoriteDto> getUserFavorites(Integer userId);

    // 특정 사용자의 찜 목록 페이징 조회
    Page<FavoriteDto> getUserFavoritesWithPaging(Integer userId, Pageable pageable);

    // 특정 사용자가 특정 장소를 찜했는지 확인
    boolean isFavorite(Integer userId, Integer placeId);

    // 찜 정보 수정
    FavoriteDto updateFavorite(Integer userId, Integer favoriteNo, UpdateFavoriteRequest request);

    // 태그별 찜 검색
    List<FavoriteDto> searchFavoritesByTag(Integer userId, String tag);

    // 특정 장소의 찜 개수
    Long getFavoriteCount(Integer placeId);
}