package com.ssafy.htrip.attraction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SigunguId implements Serializable {
    private Integer areaCode;
    private Integer sigunguCode;
}