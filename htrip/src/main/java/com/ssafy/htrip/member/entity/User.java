package com.ssafy.htrip.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    private String email;
    @Enumerated(EnumType.STRING)
    private OauthProvider oauthProvider;
    private String oauthId;
    private String name;
    private String nickname;
    private String profileImgUrl;
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime registDate;

    private LocalDateTime lastLoginAt;

    @Enumerated(EnumType.STRING)
    private Role role;

}
