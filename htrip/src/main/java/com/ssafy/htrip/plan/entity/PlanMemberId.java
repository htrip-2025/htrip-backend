package com.ssafy.htrip.plan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PlanMemberId implements Serializable {
    @Column(name = "plan_id")
    private Integer planId;

    @Column(name = "user_id")
    private Integer userId;
}