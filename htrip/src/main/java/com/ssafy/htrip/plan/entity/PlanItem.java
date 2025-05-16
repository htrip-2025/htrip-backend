package com.ssafy.htrip.plan.entity;

import com.ssafy.htrip.attraction.entity.Attraction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "plan_items")
@Getter
@Setter
public class PlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id", nullable = false)
    private PlanDay planDay;

    // Attraction(place) 과 N:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Attraction attraction;

    private Integer sequence;

    private LocalTime startTime;

    private LocalTime endTime;

    @Column(columnDefinition = "TEXT")
    private String memo;
}
