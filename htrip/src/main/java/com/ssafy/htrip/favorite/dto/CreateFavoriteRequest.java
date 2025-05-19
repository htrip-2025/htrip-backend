package com.ssafy.htrip.favorite.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFavoriteRequest {
    private Integer placeId;
    private String memo;
    private String tag;
}