package com.ssafy.htrip.attraction.service;

import com.ssafy.htrip.attraction.dto.AreaDto;
import com.ssafy.htrip.attraction.dto.SigunguDto;

import java.util.List;

public interface AreaService {
    public List<AreaDto> getAllAreas();
    public AreaDto getAreaById(int id);

    List<SigunguDto> getSigungusByArea(Integer areaCode);
}
