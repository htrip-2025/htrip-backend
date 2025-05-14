package com.ssafy.htrip.attraction.service;

import com.ssafy.htrip.attraction.entity.Attraction;
import com.ssafy.htrip.attraction.repository.AttractionRepository;
import jakarta.persistence.Table;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class AttractionServiceImpl implements AttractionService {

    private final AttractionRepository repo;

    public AttractionServiceImpl(AttractionRepository repo) {
        this.repo = repo;
    }

    @Override
    public Attraction create(Attraction attraction) {
        return repo.save(attraction);
    }

    @Override
    public Attraction update(Attraction attraction) {
        return repo.save(attraction);
    }//알단 패스

    @Override
    public Attraction findById(long placeId) throws NotFoundException {
        return repo.findById(placeId).orElseThrow(()->new NotFoundException("Attraction not found" + placeId));
    }

    @Override
    public List<Attraction> findAll() {
        return repo.findAll();
    }

    @Override
    public void deleteById(long placeId) throws NotFoundException {
        if(!repo.existsById(placeId)){
            throw new NotFoundException("Attraction not found" + placeId);
        }
        repo.deleteById(placeId);
    }
}
