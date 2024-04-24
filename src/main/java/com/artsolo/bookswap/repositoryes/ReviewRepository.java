package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.models.CompositeKey;
import com.artsolo.bookswap.models.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, CompositeKey> {
    Page<Review> findByBookId(Long bookId, Pageable pageable);
}
