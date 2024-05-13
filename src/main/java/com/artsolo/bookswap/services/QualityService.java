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

    public Quality addNewQuality(String quality) {
        Quality newQuality = Quality.builder().quality(quality).build();
        return qualityRepository.save(newQuality);
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
