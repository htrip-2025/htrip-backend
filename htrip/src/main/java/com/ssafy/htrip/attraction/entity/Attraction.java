package com.ssafy.htrip.attraction.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "attraction")
@Getter
@Setter
public class Attraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Integer placeId;

    @Column(name = "content_id", length = 50)
    private String contentId;

    @Column(name = "content_type_id", length = 10)
    private String contentTypeId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "created_time", length = 14)
    private String createdTime;

    @Column(name = "modified_time", length = 14)
    private String modifiedTime;

    @Column(name = "telephone", length = 50)
    private String telephone;

    @Column(name = "address1", length = 255)
    private String address1;

    @Column(name = "address2", length = 100)
    private String address2;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "category1", length = 10)
    private String category1;

    @Column(name = "category2", length = 10)
    private String category2;

    @Column(name = "category3", length = 10)
    private String category3;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude",nullable = false)
    private Double longitude;

    @Column(name = "map_level", length = 5)
    private String mapLevel;

    @Column(name = "first_image_url", length = 255)
    private String firstImageUrl;

    @Column(name = "first_image_thumbnail_url", length = 255)
    private String firstImageThumbnailUrl;

    @Column(name = "copyright_division_code", length = 10)
    private String copyrightDivisionCode;

    @Column(name = "booktour_info", length = 10)
    private String booktourInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_code", nullable = false)
    private Area area;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sigungu_code", nullable = false)
    private Sigungu sigungu;

    // 기본 생성자
    public Attraction() {}

    // 필요한 생성자, Getter/Setter 생략
}
