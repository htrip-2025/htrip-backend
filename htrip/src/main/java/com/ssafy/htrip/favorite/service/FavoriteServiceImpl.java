package com.ssafy.htrip.favorite.service;

import com.ssafy.htrip.attraction.dto.AttractionDto;
import com.ssafy.htrip.attraction.entity.Attraction;
import com.ssafy.htrip.attraction.repository.AreaRepository;
import com.ssafy.htrip.attraction.repository.AttractionRepository;
import com.ssafy.htrip.common.entity.User;
import com.ssafy.htrip.common.repository.UserRepository;
import com.ssafy.htrip.favorite.dto.CreateFavoriteRequest;
import com.ssafy.htrip.favorite.dto.FavoriteDto;
import com.ssafy.htrip.favorite.dto.UpdateFavoriteRequest;
import com.ssafy.htrip.favorite.entity.Favorite;
import com.ssafy.htrip.favorite.repository.FavoriteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final AttractionRepository attractionRepository;
    private final AreaRepository areaRepository;

    @Override
    @Transactional
    public FavoriteDto addFavorite(Integer userId, CreateFavoriteRequest request) {
        // 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        // 장소 존재 확인
        Attraction attraction = attractionRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new EntityNotFoundException("Attraction not found: " + request.getPlaceId()));

        // 이미 찜한 장소인지 확인
        if (favoriteRepository.existsByUserUserIdAndAttractionPlaceId(userId, request.getPlaceId())) {
            throw new IllegalStateException("Already added to favorites");
        }

        // 새 찜 생성
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setAttraction(attraction);
        favorite.setMemo(request.getMemo());
        favorite.setTag(request.getTag());
        favorite.setCreateAt(LocalDateTime.now());

        Favorite savedFavorite = favoriteRepository.save(favorite);
        return toDto(savedFavorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Integer userId, Integer favoriteNo) {
        Favorite favorite = favoriteRepository.findById(favoriteNo)
                .orElseThrow(() -> new EntityNotFoundException("Favorite not found: " + favoriteNo));

        // 해당 찜이 해당 사용자의 것인지 확인
        if (!favorite.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to delete this favorite");
        }

        favoriteRepository.delete(favorite);
    }

    @Override
    @Transactional
    public void removeFavoriteByUserAndPlace(Integer userId, Integer placeId) {
        Favorite favorite = favoriteRepository.findByUserIdAndPlaceId(userId, placeId)
                .orElseThrow(() -> new IllegalArgumentException("Favorite not found for user and place"));

        favoriteRepository.delete(favorite);
    }

    @Override
    public List<FavoriteDto> getUserFavorites(Integer userId) {
        List<Favorite> favorites = favoriteRepository.findByUserIdWithAttraction(userId);
        return favorites.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<FavoriteDto> getUserFavoritesWithPaging(Integer userId, Pageable pageable) {
        Page<Favorite> favoritesPage = favoriteRepository.findByUserUserIdWithPaging(userId, pageable);
        return favoritesPage.map(this::toDto);
    }

    @Override
    public boolean isFavorite(Integer userId, Integer placeId) {
        return favoriteRepository.existsByUserUserIdAndAttractionPlaceId(userId, placeId);
    }

    @Override
    @Transactional
    public FavoriteDto updateFavorite(Integer userId, Integer favoriteNo, UpdateFavoriteRequest request) {
        Favorite favorite = favoriteRepository.findById(favoriteNo)
                .orElseThrow(() -> new EntityNotFoundException("Favorite not found: " + favoriteNo));

        // 해당 찜이 해당 사용자의 것인지 확인
        if (!favorite.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to update this favorite");
        }

        // 메모와 태그 업데이트
        favorite.setMemo(request.getMemo());
        favorite.setTag(request.getTag());

        Favorite updatedFavorite = favoriteRepository.save(favorite);
        return toDto(updatedFavorite);
    }

    @Override
    public List<FavoriteDto> searchFavoritesByTag(Integer userId, String tag) {
        List<Favorite> favorites = favoriteRepository.findByUserIdAndTagContaining(userId, tag);
        return favorites.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long getFavoriteCount(Integer placeId) {
        return favoriteRepository.countByPlaceId(placeId);
    }

    private FavoriteDto toDto(Favorite favorite) {
        return FavoriteDto.builder()
                .favoriteNo(favorite.getFavoriteNo())
                .userId(favorite.getUser().getUserId())
                .placeId(favorite.getAttraction().getPlaceId())
                .createAt(favorite.getCreateAt())
                .memo(favorite.getMemo())
                .tag(favorite.getTag())
                .attraction(toAttractionDto(favorite.getAttraction()))
                .build();
    }

    private AttractionDto toAttractionDto(Attraction attraction) {
        return AttractionDto.builder()
                .placeId(attraction.getPlaceId())
                .title(attraction.getTitle())
                .telephone(attraction.getTelephone())
                .address1(attraction.getAddress1())
                .address2(attraction.getAddress2())
                .latitude(attraction.getLatitude())
                .longitude(attraction.getLongitude())
                .firstImageUrl(attraction.getFirstImageUrl())
                .firstImageThumbnailUrl(attraction.getFirstImageThumbnailUrl())
                .areaCode(attraction.getAreaCode())
                .sigunguCode(attraction.getSigunguCode())
                .build();
    }
}