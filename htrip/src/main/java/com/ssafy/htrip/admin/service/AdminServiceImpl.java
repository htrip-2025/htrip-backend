package com.ssafy.htrip.admin.service;

import com.ssafy.htrip.admin.dto.AdminCheckDto;
import com.ssafy.htrip.admin.dto.AdminMemberDto;
import com.ssafy.htrip.admin.dto.AdminStatsDto;
import com.ssafy.htrip.admin.dto.UpdateMemberRequest;
import com.ssafy.htrip.attraction.repository.AttractionRepository;
import com.ssafy.htrip.common.entity.Role;
import com.ssafy.htrip.common.entity.User;
import com.ssafy.htrip.common.repository.UserRepository;
import com.ssafy.htrip.favorite.repository.FavoriteRepository;
import com.ssafy.htrip.plan.repository.PlanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final PlanRepository planRepository;
    private final AttractionRepository attractionRepository;

    @Override
    public AdminCheckDto  getMember(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        AdminCheckDto adminCheckDto = new AdminCheckDto();
        adminCheckDto.setUserId(userId);
        adminCheckDto.setName(user.getName());
        adminCheckDto.setRole(user.getRole());
        return adminCheckDto;
    }

    @Override
    public Page<AdminMemberDto> getAllMembers(String keyword, String provider, String role, Pageable pageable) {
        // 현재는 간단한 구현 - 향후 QueryDSL이나 Specification으로 개선 가능
        Page<User> users;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // 키워드 검색 (이름, 닉네임, 이메일)
            users = userRepository.findByNameContainingOrNicknameContainingOrEmailContaining(
                    keyword, keyword, keyword, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return users.map(this::toAdminMemberDto);
    }

    @Override
    public AdminMemberDto getMemberDetail(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        return toAdminMemberDto(user);
    }

    @Override
    @Transactional
    public AdminMemberDto updateMember(Integer userId, UpdateMemberRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        // 닉네임 수정
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            user.setNickname(request.getNickname().trim());
        }

        // 역할 변경
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        User updatedUser = userRepository.save(user);
        return toAdminMemberDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteMember(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        // 관리자는 삭제 불가 (추가 보안)
        if (Role.ADMIN.equals(user.getRole())) {
            throw new IllegalArgumentException("관리자 계정은 삭제할 수 없습니다.");
        }

        userRepository.delete(user);
    }

    @Override
    public AdminStatsDto getAdminStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);

        // 전체 통계
        Long totalMembers = userRepository.count();
        Long totalPlans = planRepository.count();
        Long totalAttractions = attractionRepository.count();
        Long totalFavorites = favoriteRepository.count();

        // 이번 주 통계
        Long newMembersThisWeek = userRepository.countByRegistDateAfter(weekAgo);

        // OAuth 제공자별 통계
        Map<String, Long> membersByProvider = userRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        user -> user.getOauthProvider().name(),
                        Collectors.counting()
                ));

        // 역할별 통계
        Long adminCount = userRepository.countByRole(Role.ADMIN);
        Long userCount = userRepository.countByRole(Role.USER);

        return AdminStatsDto.builder()
                .totalMembers(totalMembers)
                .totalActivePlans(totalPlans)
                .totalAttractions(totalAttractions)
                .totalFavorites(totalFavorites)
                .newMembersThisWeek(newMembersThisWeek)
                .membersByProvider(membersByProvider)
                .adminCount(adminCount)
                .userCount(userCount)
                .lastUpdated(now)
                .build();
    }

    @Override
    public Map<String, Long> getDailyRegistrations(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);

        // 간단한 구현 - 실제로는 GROUP BY DATE 쿼리 사용
        Map<String, Long> dailyStats = new LinkedHashMap<>();

        for (int i = days; i >= 0; i--) {
            LocalDateTime dayStart = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime dayEnd = dayStart.plusDays(1);

            Long count = userRepository.countByRegistDateBetween(dayStart, dayEnd);
            String dateKey = dayStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            dailyStats.put(dateKey, count);
        }

        return dailyStats;
    }

    @Override
    public Map<String, Long> getOAuthProviderStats() {
        return userRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        user -> user.getOauthProvider().name(),
                        Collectors.counting()
                ));
    }

    private AdminMemberDto toAdminMemberDto(User user) {
        // 추가 통계 조회
        Long favoriteCount = favoriteRepository.countByUserUserId(user.getUserId());
        Long planCount = planRepository.countByUserUserId(user.getUserId());

        return AdminMemberDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .profileImgUrl(user.getProfileImgUrl())
                .oauthProvider(user.getOauthProvider())
                .oauthId(user.getOauthId())
                .role(user.getRole())
                .registDate(user.getRegistDate())
                .lastLoginAt(user.getLastLoginAt())
                .favoriteCount(favoriteCount)
                .planCount(planCount)
                .isActive(true) // 향후 확장
                .build();
    }
}