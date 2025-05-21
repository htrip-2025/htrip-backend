// com.ssafy.htrip.attraction.service.AttractionServiceImpl.java
package com.ssafy.htrip.attraction.service;

import com.ssafy.htrip.attraction.dto.AttractionDto;
import com.ssafy.htrip.attraction.entity.Area;
import com.ssafy.htrip.attraction.entity.Attraction;
import com.ssafy.htrip.attraction.entity.Sigungu;
import com.ssafy.htrip.attraction.entity.SigunguId;
import com.ssafy.htrip.attraction.repository.AreaRepository;
import com.ssafy.htrip.attraction.repository.AttractionRepository;
import com.ssafy.htrip.attraction.repository.SigunguRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttractionServiceImpl implements AttractionService {

    private final AttractionRepository attractionRepository;
    private final AreaRepository areaRepository;
    private final SigunguRepository sigunguRepository;

    @Override
    public AttractionDto findById(Integer placeId) throws NotFoundException {
        Attraction a = attractionRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException("Attraction not found: " + placeId));

        // loadRelatedEntities 메서드 활용
        try {
            loadRelatedEntities(a);
        } catch (Exception e) {
            // 예외 처리 방법 선택
            throw new NotFoundException(e.getMessage());
        }

        return toDto(a);
    }

    @SneakyThrows
    @Override
    public List<AttractionDto> findRandom(int n) {
        List<Attraction> attractions = attractionRepository.findRandom(n);
        // 각 엔티티에 area와 sigungu 정보를 로드해야 함
        attractions.forEach(this::loadRelatedEntities);
        return attractions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public List<AttractionDto> searchByKeyword(String keyword) {
        List<Attraction> attractions = attractionRepository.findByTitleContainingIgnoreCase(keyword);
        // 각 엔티티에 area와 sigungu 정보를 로드해야 함
        attractions.forEach(this::loadRelatedEntities);
        return attractions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 연관 엔티티 로드 메서드 수정 (필요에 따라 예외 발생)
    private void loadRelatedEntities(Attraction attraction) throws NotFoundException {
        try {
            if (attraction.getAreaCode() != null) {
                areaRepository.findById(attraction.getAreaCode())
                        .ifPresent(area -> {
                            if (attraction.getSigunguCode() != null) {
                                SigunguId sigunguId = new SigunguId(area.getAreaCode(), attraction.getSigunguCode());
                                sigunguRepository.findById(sigunguId)
                                        .ifPresent(sigungu -> attraction.setAreaAndSigungu(area, sigungu));
                            } else {
                                attraction.setArea(area);
                            }
                        });
            }
        } catch (Exception e) {
            log.warn("관련 엔티티 로드 실패 (attraction={}): {}",
                    attraction.getPlaceId(), e.getMessage());
        }
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
                .areaCode(a.getAreaCode())
                .areaName(a.getArea() != null ? a.getArea().getName() : null)
                .sigunguCode(a.getSigunguCode())
                .sigunguName(a.getSigungu() != null ? a.getSigungu().getName() : null)
                .build();
    }
}
