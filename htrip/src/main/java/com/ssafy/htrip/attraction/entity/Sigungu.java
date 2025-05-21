package com.ssafy.htrip.attraction.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sigungu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sigungu {
    @EmbeddedId
    private SigunguId id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    // 편의 메서드
    public Integer getAreaCode() {
        return id != null ? id.getAreaCode() : null;
    }

    public Integer getSigunguCode() {
        return id != null ? id.getSigunguCode() : null;
    }

    // MapsId를 사용한 관계 설정 (선택사항)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("areaCode")
    @JoinColumn(name = "area_code")
    private Area area;
}