package com.ssafy.htrip.common.repository;

import com.ssafy.htrip.common.entity.OauthProvider;
import com.ssafy.htrip.common.entity.Role;
import com.ssafy.htrip.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByOauthProviderAndOauthId(OauthProvider oauthProvider, String oauthId);
    // 관리자용 검색 메서드들 추가
    Page<User> findByNameContainingOrNicknameContainingOrEmailContaining(
            String name, String nickname, String email, Pageable pageable);

    // 통계용 메서드들
    Long countByRegistDateAfter(LocalDateTime date);
    Long countByRegistDateBetween(LocalDateTime start, LocalDateTime end);
    Long countByRole(Role role);

}
