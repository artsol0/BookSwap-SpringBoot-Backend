package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.models.CompositeKey;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.models.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, CompositeKey> {
    List<Wishlist> findByUser(User user);
}
