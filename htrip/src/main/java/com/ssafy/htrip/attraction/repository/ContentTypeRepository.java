package com.ssafy.htrip.attraction.repository;

import com.ssafy.htrip.attraction.entity.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentTypeRepository extends JpaRepository<ContentType, Integer> {
}