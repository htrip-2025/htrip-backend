package com.ssafy.htrip.member.service;

import com.ssafy.htrip.common.entity.User;
import com.ssafy.htrip.common.repository.UserRepository;
import com.ssafy.htrip.favorite.repository.FavoriteRepository;
import com.ssafy.htrip.member.dto.MemberProfileDto;
import com.ssafy.htrip.member.dto.MemberStatsDto;
import com.ssafy.htrip.member.dto.UpdateProfileRequest;
import com.ssafy.htrip.plan.repository.PlanRepository;
import com.ssafy.htrip.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final PlanRepository planRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public MemberProfileDto getProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        // 통계 정보 조회
        Long favoriteCount = favoriteRepository.countByUserUserId(userId);
        Long planCount = planRepository.countByUserUserId(userId);

        return MemberProfileDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .profileImgUrl(user.getProfileImgUrl())
                .registDate(user.getRegistDate())
                .lastLoginAt(user.getLastLoginAt())
                .oauthProvider(user.getOauthProvider())
                .role(user.getRole())
                .favoriteCount(favoriteCount)
                .planCount(planCount)
                .build();
    }

    @Override
    @Transactional
    public MemberProfileDto updateProfile(Integer userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        // 닉네임과 프로필 이미지만 수정 가능
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            user.setNickname(request.getNickname().trim());
        }

        if (request.getProfileImgUrl() != null) {
            user.setProfileImgUrl(request.getProfileImgUrl());
        }

        User updatedUser = userRepository.save(user);

        // 업데이트된 프로필 반환
        return getProfile(updatedUser.getUserId());
    }

    @Override
    public MemberStatsDto getStats(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        // 각종 통계 조회
        Long favoriteCount = favoriteRepository.countByUserUserId(userId);
        Long planCount = planRepository.countByUserUserId(userId);
        Long reviewCount = reviewRepository.countByUserUserId(userId);
        // Long boardCount = boardRepository.countByUserUserId(userId);

        // 완료된 여행 계획 수
        Long completedPlanCount = planRepository.countCompletedPlansByUserId(userId);

        // 가입 후 일수 계산
        long daysSinceJoin = 0;
        if (user.getRegistDate() != null) {
            daysSinceJoin = ChronoUnit.DAYS.between(
                    user.getRegistDate().toLocalDate(),
                    LocalDateTime.now().toLocalDate()
            );
        }

        return MemberStatsDto.builder()
                .favoriteCount(favoriteCount)
                .planCount(planCount)
                .reviewCount(reviewCount)
                // .boardCount(boardCount)
                .completedPlanCount(completedPlanCount)
                .joinDate(user.getRegistDate())
                .daysSinceJoin(daysSinceJoin)
                .build();
    }

    @Override
    @Transactional
    public void deleteAccount(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        // 연관된 데이터들은 CASCASE DELETE로 자동 삭제됨
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void updateLastLoginAt(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }
}