package com.ssafy.htrip.attraction.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sigungu")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sigungu {
    @Id
    @Column(name = "code")
    private Integer sigunguCode;

    @Column(name = "name", nullable = false, length = 50)
    private String name;
}
