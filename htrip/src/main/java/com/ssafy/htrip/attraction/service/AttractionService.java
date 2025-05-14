package com.ssafy.htrip.attraction.service;

import com.ssafy.htrip.attraction.entity.Attraction;
import org.apache.ibatis.javassist.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface AttractionService {
    Attraction create(Attraction attraction);
    Attraction update(Attraction attraction);
    Attraction findById(long placeId) throws NotFoundException;
    List<Attraction> findAll();
    void deleteById(long placeId) throws NotFoundException;
}
