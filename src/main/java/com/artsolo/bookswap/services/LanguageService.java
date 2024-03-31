package com.artsolo.bookswap.services;

import com.artsolo.bookswap.models.Language;
import com.artsolo.bookswap.repositoryes.LanguageRepository;
import org.springframework.stereotype.Service;

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

    public boolean deleteLanguage(Language language) {
        languageRepository.deleteById(language.getId());
        return !languageRepository.existsById(language.getId());
    }

    public Optional<Language> getLanguageById(Long id) {return languageRepository.findById(id);}

    public List<Language> getAllLanguages() {return languageRepository.findAll();}
}
