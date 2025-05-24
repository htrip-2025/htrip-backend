// com.ssafy.htrip.attraction.service.AttractionServiceImpl.java
package com.ssafy.htrip.attraction.service;

import com.ssafy.htrip.attraction.dto.AttractionDto;
import com.ssafy.htrip.attraction.dto.AttractionSearchRequest;
import com.ssafy.htrip.attraction.entity.Attraction;
import com.ssafy.htrip.attraction.entity.SigunguId;
import com.ssafy.htrip.attraction.repository.AreaRepository;
import com.ssafy.htrip.attraction.repository.AttractionRepository;
import com.ssafy.htrip.attraction.repository.AttractionContentTypeRepository;
import com.ssafy.htrip.attraction.repository.SigunguRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final AttractionCategoryService attractionCategoryService; // 추가: 카테고리 서비스
    private final AttractionContentTypeRepository attractionContentTypeRepository; // 추가: 컨텐츠 타입 레포지토리

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

    @Override
    public List<AttractionDto> findRandom(int n) {
        List<Attraction> attractions = attractionRepository.findRandom(n);
        // 각 엔티티에 area와 sigungu 정보를 로드해야 함
        attractions.forEach(this::loadRelatedEntities);
        return attractions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttractionDto> searchByKeyword(String keyword) {
        List<Attraction> attractions = attractionRepository.findByTitleContainingIgnoreCase(keyword);
        // 각 엔티티에 area와 sigungu 정보를 로드해야 함
        attractions.forEach(this::loadRelatedEntities);
        return attractions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AttractionDto> searchAttractions(AttractionSearchRequest request, Pageable pageable) {
        // 모든 필터 조건이 null인 경우 전체 조회
        if (isAllFiltersEmpty(request)) {
            return attractionRepository.findAll(pageable).map(this::toDto);
        }

        String category1 = null;
        String category2 = null;
        String category3 = null;

        // 카테고리 코드 길이에 따라 적절한 필드에 설정
        if (request.getCategoryCode() != null && !request.getCategoryCode().isEmpty()) {
            int length = request.getCategoryCode().length();
            if (length == 3) {
                category1 = request.getCategoryCode();
            } else if (length == 5) {
                category2 = request.getCategoryCode();
            } else if (length == 9) {
                category3 = request.getCategoryCode();
            }
        }

        // 고급 필터링 쿼리 사용
        Page<Attraction> attractions = attractionRepository.findWithFilters(
                request.getKeyword(),
                request.getAreaCode(),
                request.getSigunguCode(),
                request.getContentTypeId(),
                category1,
                category2,
                category3,
                pageable
        );

        return attractions.map(this::toDto);
    }

    private boolean isAllFiltersEmpty(AttractionSearchRequest request) {
        return (request.getKeyword() == null || request.getKeyword().trim().isEmpty()) &&
                request.getAreaCode() == null &&
                request.getSigunguCode() == null &&
                (request.getContentTypeId() == null || request.getContentTypeId().trim().isEmpty()) &&
                (request.getCategoryCode() == null || request.getCategoryCode().trim().isEmpty());
    }

    // 연관 엔티티 로드 메서드 수정 (필요에 따라 예외 발생)
    private void loadRelatedEntities(Attraction attraction) {
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
        AttractionDto.AttractionDtoBuilder builder = AttractionDto.builder()
                .placeId(a.getPlaceId())
                .contentTypeId(a.getContentTypeId())
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
                .sigunguCode(a.getSigunguCode());

        // 컨텐츠 타입 이름 조회 및 추가
        if (a.getContentTypeId() != null && !a.getContentTypeId().isEmpty()) {
            try {
                Integer contentTypeIdInt = Integer.parseInt(a.getContentTypeId());
                attractionContentTypeRepository.findById(contentTypeIdInt).ifPresent(
                        contentType -> builder.contentTypeName(contentType.getContentName())
                );
            } catch (NumberFormatException e) {
                log.warn("유효하지 않은 contentTypeId: {}", a.getContentTypeId());
            }
        }

        // 카테고리 정보 조회 및 추가
        builder.categories(attractionCategoryService.getAttractionCategories(a.getCategory3()));

        return builder.build();
    }
}