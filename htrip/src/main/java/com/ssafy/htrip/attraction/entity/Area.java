package com.ssafy.htrip.attraction.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "area")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Area {
    @Id
    @Column(name = "code")
    private Integer areaCode;

    @Column(name = "name", nullable = false, length = 50)
    private String name;
}
