package com.ssafy.htrip.attraction.controller;

import com.ssafy.htrip.attraction.entity.Attraction;
import com.ssafy.htrip.attraction.service.AttractionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/travel")
public class TravelController {

    private final AttractionService aService;

    public TravelController(AttractionService aService) {
        this.aService = aService;
    }
    @GetMapping("{/placeId}")
    public Attraction findById(long placeId) throws NotFoundException {
        return aService.findById(placeId);
    }
}
