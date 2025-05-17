package com.ssafy.htrip.common.repository;

import com.ssafy.htrip.common.entity.OauthProvider;
import com.ssafy.htrip.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByOauthProviderAndOauthId(OauthProvider oauthProvider, String oauthId);
}
