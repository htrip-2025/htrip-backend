package com.ssafy.htrip.attraction.controller;

import com.ssafy.htrip.attraction.dto.AttractionDto;
import com.ssafy.htrip.attraction.entity.Attraction;
import com.ssafy.htrip.attraction.service.AttractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelController {

    private final AttractionService aService;

    @GetMapping("/{placeId}")
    public ResponseEntity<AttractionDto> findById(Integer placeId) throws Throwable {
        AttractionDto dto = aService.findById(placeId);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/")
    public ResponseEntity<List<AttractionDto>> previewRandom(@RequestParam(defaultValue = "6") int n) {
        List<AttractionDto> dto = aService.findRandom(n);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AttractionDto>> search(
            @RequestParam(defaultValue = "") String keyword) {
        return ResponseEntity.ok(aService.searchByKeyword(keyword));
    }

}
