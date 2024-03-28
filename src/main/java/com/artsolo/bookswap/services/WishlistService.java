package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.book.GetBookResponse;
import com.artsolo.bookswap.models.*;
import com.artsolo.bookswap.repositoryes.BookRepository;
import com.artsolo.bookswap.repositoryes.WishlistRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<GetBookResponse> getAllWishlistBooks(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        List<Wishlist> wishlists = wishlistRepository.findByUserId(user.getId());
        List<GetBookResponse> getBookResponses = new ArrayList<>();
        for (Wishlist wishlist : wishlists) {
            getBookResponses.add(GetBookResponse.builder()
                            .id(wishlist.getBook().getId())
                            .title(wishlist.getBook().getTitle())
                            .author(wishlist.getBook().getAuthor())
                            .genres(wishlist.getBook().getGenres().stream().map(Genre::getGenre).collect(Collectors.toList()))
                            .quality(wishlist.getBook().getQuality().getQuality())
                            .status(wishlist.getBook().getStatus().getStatus())
                            .language(wishlist.getBook().getLanguage().getLanguage())
                            .photo(wishlist.getBook().getPhoto())
                            .build());
        }
        return getBookResponses;
    }
}
