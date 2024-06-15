package com.artsolo.bookswap.review;

import com.artsolo.bookswap.compositekey.CompositeKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, CompositeKey> {
    Page<Review> findByBookId(Long bookId, Pageable pageable);
}
