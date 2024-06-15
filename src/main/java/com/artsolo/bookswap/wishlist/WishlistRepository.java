package com.artsolo.bookswap.wishlist;

import com.artsolo.bookswap.compositekey.CompositeKey;
import com.artsolo.bookswap.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, CompositeKey> {
    List<Wishlist> findByUser(User user);
}
