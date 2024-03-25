package com.artsolo.bookswap.services;

import com.artsolo.bookswap.models.Quality;
import com.artsolo.bookswap.repositoryes.QualityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public boolean deleteQualityById(Long id) {
        Optional<Quality> quality = qualityRepository.findById(id);
        if (quality.isPresent()) {
            qualityRepository.deleteById(quality.get().getId());
            return !qualityRepository.existsById(quality.get().getId());
        }
        return false;
    }

    public Quality getQualityById(Long id) {
        return qualityRepository.findById(id).orElse(null);
    }

    public List<Quality> getAllQualities() {return qualityRepository.findAll();}
}
