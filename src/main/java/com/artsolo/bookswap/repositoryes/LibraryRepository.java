package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.models.Library;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryRepository extends JpaRepository<Library, Long> {
}
