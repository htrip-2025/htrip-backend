package com.ssafy.htrip.attraction.repository;

import com.ssafy.htrip.attraction.entity.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttractionRepository extends JpaRepository<Attraction, Long> {
}
