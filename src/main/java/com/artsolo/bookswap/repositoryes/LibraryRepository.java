package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.models.CompositeKey;
import com.artsolo.bookswap.models.Library;
import com.artsolo.bookswap.models.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibraryRepository extends JpaRepository<Library, CompositeKey> {
    List<Library> findAllByUserId(Long userId);
    List<Library> findAllByBookId(Long bookId);
}
