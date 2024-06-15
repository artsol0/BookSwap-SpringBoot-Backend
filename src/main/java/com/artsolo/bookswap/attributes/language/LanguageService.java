package com.artsolo.bookswap.attributes.language;

import com.artsolo.bookswap.exceptions.NoDataFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageService {
    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public Language addNewLanguage(String language) {
        Language newLanguage = Language.builder().language(language).build();
        return languageRepository.save(newLanguage);
    }

    public boolean deleteLanguage(Language language) {
        languageRepository.deleteById(language.getId());
        return !languageRepository.existsById(language.getId());
    }

    public Language getLanguageById(Long id) {
        return languageRepository.findById(id).orElseThrow(() -> new NoDataFoundException("Language", id));
    }

    public List<Language> getAllLanguages() {return languageRepository.findAll();}
}
