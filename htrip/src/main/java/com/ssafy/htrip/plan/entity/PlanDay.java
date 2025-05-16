package com.ssafy.htrip.plan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "plan_days")
@Getter
@Setter
public class PlanDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dayId;

    private Integer dayDate;


    // Plan(여행계획)과 N:1 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    // PlanDay → PlanItem 1:N
    @OneToMany(
            mappedBy = "planDay",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PlanItem> items = new ArrayList<>();

}
