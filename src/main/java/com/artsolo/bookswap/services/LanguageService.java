package com.artsolo.bookswap.services;

import com.artsolo.bookswap.models.Language;
import com.artsolo.bookswap.repositoryes.LanguageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LanguageService {
    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public boolean addNewLanguage(String language) {
        Language newLanguage = new Language();
        newLanguage.setLanguage(language);
        newLanguage = languageRepository.save(newLanguage);
        return languageRepository.existsById(newLanguage.getId());
    }

    public boolean deleteLanguageById(Long id) {
        Optional<Language> language = languageRepository.findById(id);
        if (language.isPresent()) {
            languageRepository.deleteById(language.get().getId());
            return !languageRepository.existsById(language.get().getId());
        }
        return false;
    }

    public Language getLanguageById(Long id) {return languageRepository.findById(id).orElse(null);}

    public List<Language> getAllLanguages() {return languageRepository.findAll();}
}
