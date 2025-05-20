package com.ssafy.htrip.plan.entity;

import com.ssafy.htrip.common.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trip_plan")
@Getter
@Setter
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Integer planId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime updateDate;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic = true;

    // Plan → PlanDay (1:N)
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanDay> days = new ArrayList<>();

    // Plan → PlanMember (1:N) - 추가
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanMember> members = new ArrayList<>();
}
