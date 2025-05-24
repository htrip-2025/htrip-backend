package com.ssafy.htrip.attraction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttractionContentTypeDto {
    private Integer contentTypeId;
    private String contentName;
}