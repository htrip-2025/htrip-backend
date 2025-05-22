// com.ssafy.htrip.attraction.service.AttractionService.java
package com.ssafy.htrip.attraction.service;

import com.ssafy.htrip.attraction.dto.AttractionDto;
import com.ssafy.htrip.attraction.dto.AttractionSearchRequest;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AttractionService {
    AttractionDto findById(Integer placeId) throws NotFoundException;
    List<AttractionDto> findRandom(int n);
    List<AttractionDto> searchByKeyword(String keyword);
    Page<AttractionDto> searchAttractions(AttractionSearchRequest request, Pageable pageable);
}
