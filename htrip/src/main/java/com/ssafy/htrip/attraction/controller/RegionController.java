package com.ssafy.htrip.attraction.controller;

import com.ssafy.htrip.attraction.dto.AreaDto;
import com.ssafy.htrip.attraction.dto.SigunguDto;
import com.ssafy.htrip.attraction.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/region")
@RequiredArgsConstructor
public class RegionController {

    private final AreaService areaService;

    @GetMapping("/")
    public ResponseEntity<List<AreaDto>> getAllAreas() {
        List<AreaDto> areas = areaService.getAllAreas();
        return ResponseEntity.ok(areas);
    }

    @GetMapping("/{areaCode}/sigungu")
    public ResponseEntity<List<SigunguDto>> getSigungusByArea(@PathVariable Integer areaCode) {
        List<SigunguDto> sigungus = areaService.getSigungusByArea(areaCode);
        return ResponseEntity.ok(sigungus);
    }
}

