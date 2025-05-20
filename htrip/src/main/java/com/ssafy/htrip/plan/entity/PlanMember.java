package com.ssafy.htrip.plan.entity;

import com.ssafy.htrip.common.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "plan_member")
@Getter
@Setter
public class PlanMember {
    @EmbeddedId
    private PlanMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("planId")
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_no", nullable = false)
    private MemberRole memberRole;

    @Column(length = 20)
    private String nickname;
}
