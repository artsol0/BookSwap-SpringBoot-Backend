package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Long> {
}
