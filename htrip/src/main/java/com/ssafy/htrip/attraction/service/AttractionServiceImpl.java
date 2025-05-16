// com.ssafy.htrip.attraction.service.AttractionServiceImpl.java
package com.ssafy.htrip.attraction.service;

import com.ssafy.htrip.attraction.dto.AttractionDto;
import com.ssafy.htrip.attraction.entity.Attraction;
import com.ssafy.htrip.attraction.repository.AttractionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttractionServiceImpl implements AttractionService {

    private final AttractionRepository repo;

    @Override
    public AttractionDto findById(Integer placeId) throws NotFoundException {
        Attraction a = repo.findById(placeId)
                .orElseThrow(() -> new NotFoundException("Attraction not found: " + placeId));
        return toDto(a);
    }

    @Override
    public List<AttractionDto> findRandom(int n) {
        return repo.findRandom(n).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttractionDto> searchByKeyword(String keyword) {
        return repo.findByTitleContainingIgnoreCase(keyword).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private AttractionDto toDto(Attraction a) {
        return AttractionDto.builder()
                .placeId(a.getPlaceId())
                .title(a.getTitle())
                .telephone(a.getTelephone())
                .address1(a.getAddress1())
                .address2(a.getAddress2())
                .zipCode(a.getZipCode())
                .category1(a.getCategory1())
                .category2(a.getCategory2())
                .category3(a.getCategory3())
                .latitude(a.getLatitude())
                .longitude(a.getLongitude())
                .mapLevel(a.getMapLevel())
                .firstImageUrl(a.getFirstImageUrl())
                .firstImageThumbnailUrl(a.getFirstImageThumbnailUrl())
                .copyrightDivisionCode(a.getCopyrightDivisionCode())
                .booktourInfo(a.getBooktourInfo())
                // 연관 엔티티에서 필요한 필드만 꺼내 담기
                .areaCode(a.getArea().getAreaCode())
                .areaName(a.getArea().getName())
                .sigunguCode(a.getSigungu().getSigunguCode())
                .sigunguName(a.getSigungu().getName())
                .build();
    }
}
