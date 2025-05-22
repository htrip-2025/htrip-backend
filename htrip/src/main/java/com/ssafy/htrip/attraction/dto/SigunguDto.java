package com.ssafy.htrip.attraction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SigunguDto {
    private Integer areaCode;
    private Integer sigunguCode;
    private String name;
}
