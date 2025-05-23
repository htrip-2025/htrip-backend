package com.ssafy.htrip.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Integer reviewId;
    private Integer userId;
    private String userName;
    private String userProfileImage;
    private Integer placeId;
    private String placeName;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}