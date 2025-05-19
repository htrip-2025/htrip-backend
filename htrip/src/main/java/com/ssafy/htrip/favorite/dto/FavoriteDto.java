package com.ssafy.htrip.favorite.dto;

import com.ssafy.htrip.attraction.dto.AttractionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteDto {
    private Integer favoriteNo;
    private Integer userId;
    private Integer placeId;
    private LocalDateTime createAt;
    private String memo;
    private String tag;

    // 연관된 attraction 정보
    private AttractionDto attraction;
}