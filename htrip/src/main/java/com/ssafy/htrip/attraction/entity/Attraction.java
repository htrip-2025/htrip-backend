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

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "created_time", length = 14)
    private String createdTime;

    @Column(name = "modified_time", length = 14)
    private String modifiedTime;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "address1")
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

    @Column(name = "first_image_url")
    private String firstImageUrl;

    @Column(name = "first_image_thumbnail_url")
    private String firstImageThumbnailUrl;

    @Column(name = "copyright_division_code", length = 10)
    private String copyrightDivisionCode;

    @Column(name = "booktour_info", length = 10)
    private String booktourInfo;

    @Column(name = "area_code", nullable = false)
    private Integer areaCode;

    @Column(name = "sigungu_code", nullable = false)
    private Integer sigunguCode;

    // 기본 생성자
    public Attraction() {}
    @Transient
    private Area area;

    @Transient
    private Sigungu sigungu;

    // 편의 메서드
    public void setAreaAndSigungu(Area area, Sigungu sigungu) {
        this.area = area;
        this.sigungu = sigungu;
    }
    // 필요한 생성자, Getter/Setter 생략
}
