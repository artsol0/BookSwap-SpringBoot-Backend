package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
