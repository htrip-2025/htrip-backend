package com.ssafy.htrip.attraction.repository;

import com.ssafy.htrip.attraction.entity.Sigungu;
import com.ssafy.htrip.attraction.entity.SigunguId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SigunguRepository extends JpaRepository<Sigungu, SigunguId> {
    @Query("SELECT s FROM Sigungu s WHERE s.id.areaCode = :areaCode AND s.id.sigunguCode = :sigunguCode")
    Optional<Sigungu> findByAreaCodeAndSigunguCode(
            @Param("areaCode") Integer areaCode,
            @Param("sigunguCode") Integer sigunguCode
    );

    // 지역별 시군구 목록 조회
    List<Sigungu> findByIdAreaCode(Integer areaCode);
}
