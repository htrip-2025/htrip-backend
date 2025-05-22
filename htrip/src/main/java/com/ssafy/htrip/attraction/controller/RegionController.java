package com.ssafy.htrip.attraction.controller;

import com.ssafy.htrip.attraction.dto.AreaDto;
import com.ssafy.htrip.attraction.dto.SigunguDto;
import com.ssafy.htrip.attraction.service.AreaService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/region")
@RequiredArgsConstructor
@Tag(name = "Region", description = "지역 확인 API")
public class RegionController {

    private final AreaService areaService;

    @GetMapping("/")
    public ResponseEntity<?> getAllAreas() {
        try {
            List<AreaDto> areas = areaService.getAllAreas();
            log.info("전체 지역 목록 조회 완료 - 총 {}개", areas.size());
            return ResponseEntity.ok(areas);
        } catch (Exception e) {
            log.error("지역 목록 조회 실패", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "지역 목록 조회에 실패했습니다."));
        }
    }

    @GetMapping("/{areaCode}/sigungu")
    public ResponseEntity<?> getSigungusByArea(
            @Parameter(description = "지역 코드", example = "1")
            @PathVariable Integer areaCode) {

        try {
            List<SigunguDto> sigungus = areaService.getSigungusByArea(areaCode);
            log.info("지역별 시군구 목록 조회 완료 - areaCode: {}, 총 {}개", areaCode, sigungus.size());
            return ResponseEntity.ok(sigungus);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 지역 코드 요청 - areaCode: {}", areaCode);
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("시군구 목록 조회 실패 - areaCode: {}", areaCode, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "시군구 목록 조회에 실패했습니다."));
        }
    }
}

