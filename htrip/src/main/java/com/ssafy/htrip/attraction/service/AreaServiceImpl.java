package com.ssafy.htrip.attraction.service;

import com.ssafy.htrip.attraction.dto.AreaDto;
import com.ssafy.htrip.attraction.dto.SigunguDto;
import com.ssafy.htrip.attraction.entity.Area;
import com.ssafy.htrip.attraction.entity.Sigungu;
import com.ssafy.htrip.attraction.repository.AreaRepository;
import com.ssafy.htrip.attraction.repository.SigunguRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AreaServiceImpl implements AreaService {

    private final AreaRepository areaRepository;
    private final SigunguRepository sigunguRepository;

    @Override
    public List<AreaDto> getAllAreas() {
        log.debug("전체 지역 목록 조회");

        List<Area> areas = areaRepository.findAll();

        return areas.stream()
                .map(this::toAreaDto)
                .collect(Collectors.toList());
    }

    @Override
    public AreaDto getAreaById(int id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지역: " + id));

        return toAreaDto(area);
    }

    @Override
    public List<SigunguDto> getSigungusByArea(Integer areaCode) {
        log.debug("지역별 시군구 목록 조회 - areaCode: {}", areaCode);

        // 해당 지역이 존재하는지 확인
        Area area = areaRepository.findById(areaCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지역 코드: " + areaCode));

        List<Sigungu> sigungus = sigunguRepository.findByIdAreaCode(areaCode);

        return sigungus.stream()
                .map(this::toSigunguDto)
                .collect(Collectors.toList());
    }

    private AreaDto toAreaDto(Area area) {
        return new AreaDto(area.getAreaCode(), area.getName());
    }

    private SigunguDto toSigunguDto(Sigungu sigungu) {
        return new SigunguDto(
                sigungu.getAreaCode(),
                sigungu.getSigunguCode(),
                sigungu.getName()
        );
    }
}