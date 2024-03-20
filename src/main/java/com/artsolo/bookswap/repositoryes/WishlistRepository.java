package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.models.CompositeKey;
import com.artsolo.bookswap.models.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<Wishlist, CompositeKey> {
}
