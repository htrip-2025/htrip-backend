// com.ssafy.htrip.attraction.dto.AttractionDto.java
package com.ssafy.htrip.attraction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AttractionDto {
    private Integer placeId;
    private String title;
    private String contentTypeId;
    private String contentTypeName;
    private String telephone;
    private String address1;
    private String address2;
    private String zipCode;
    private String category1;
    private String category2;
    private String category3;
    private Map<String, String> categories;
    private String mapLevel;
    private Double latitude;
    private Double longitude;
    private String firstImageUrl;
    private String firstImageThumbnailUrl;
    private String copyrightDivisionCode;
    private String booktourInfo;

    private Integer areaCode;  // 지역 이름
    private Integer sigunguCode;// 시군구 이름
}
