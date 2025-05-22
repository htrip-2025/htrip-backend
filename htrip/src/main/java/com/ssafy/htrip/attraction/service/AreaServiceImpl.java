package com.ssafy.htrip.attraction.service;

import com.ssafy.htrip.attraction.dto.AreaDto;
import com.ssafy.htrip.attraction.dto.SigunguDto;
import com.ssafy.htrip.attraction.entity.Area;
import com.ssafy.htrip.attraction.repository.AreaRepository;

import java.util.ArrayList;
import java.util.List;

public class AreaServiceImpl implements AreaService {

    AreaRepository areaRepository;

    @Override
    public List<AreaDto> getAllAreas() {
        List<Area> areas = areaRepository.findAll();
        return areas;
    }

    @Override
    public AreaDto getAreaById(int id) {
        return null;
    }

    @Override
    public List<SigunguDto> getSigungusByArea(Integer areaCode) {
        return List.of();
    }
}
