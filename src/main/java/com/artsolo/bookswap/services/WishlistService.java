package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.BookRequest;
import com.artsolo.bookswap.models.Book;
import com.artsolo.bookswap.models.CompositeKey;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.models.Wishlist;
import com.artsolo.bookswap.repositoryes.BookRepository;
import com.artsolo.bookswap.repositoryes.WishlistRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final BookRepository bookRepository;

    public WishlistService(WishlistRepository wishlistRepository, BookRepository bookRepository) {
        this.wishlistRepository = wishlistRepository;
        this.bookRepository = bookRepository;
    }

    public boolean addBookToWishlist(Long id, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            CompositeKey compositeKey = new CompositeKey(user.getId(), book.getId());
            Wishlist wishlist = new Wishlist(compositeKey, user, book);
            wishlistRepository.save(wishlist);
            return wishlistRepository.existsById(compositeKey);
        }
        return false;
    }

    public boolean removeBookFromWishlist(Long id, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            CompositeKey compositeKey = new CompositeKey(user.getId(), book.getId());
            wishlistRepository.deleteById(compositeKey);
            return !wishlistRepository.existsById(compositeKey);
        }
        return false;
    }
}
