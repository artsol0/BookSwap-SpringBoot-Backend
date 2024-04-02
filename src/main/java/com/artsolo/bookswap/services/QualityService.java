package com.artsolo.bookswap.services;

import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.models.Quality;
import com.artsolo.bookswap.repositoryes.QualityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QualityService {
    private final QualityRepository qualityRepository;

    public QualityService(QualityRepository qualityRepository) {
        this.qualityRepository = qualityRepository;
    }

    public boolean addNewQuality(String quality) {
        Quality newQuality = new Quality();
        newQuality.setQuality(quality);
        newQuality = qualityRepository.save(newQuality);
        return qualityRepository.existsById(newQuality.getId());
    }

    public boolean deleteQuality(Quality quality) {
        qualityRepository.deleteById(quality.getId());
        return !qualityRepository.existsById(quality.getId());
    }

    public Quality getQualityById(Long id) {
        return qualityRepository.findById(id).orElseThrow(() -> new NoDataFoundException("Quality", id));
    }

    public List<Quality> getAllQualities() {return qualityRepository.findAll();}
}
