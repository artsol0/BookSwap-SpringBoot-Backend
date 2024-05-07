package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.book.BookResponse;
import com.artsolo.bookswap.models.*;
import com.artsolo.bookswap.repositoryes.WishlistRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    public WishlistService(WishlistRepository wishlistRepository) {this.wishlistRepository = wishlistRepository;}

    public boolean addBookToWishlist(Book book, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        CompositeKey compositeKey = new CompositeKey(user.getId(), book.getId());
        Wishlist wishlist = new Wishlist(compositeKey, user, book);
        wishlistRepository.save(wishlist);
        return wishlistRepository.existsById(compositeKey);
    }

    public boolean removeBookFromWishlist(Book book, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        CompositeKey compositeKey = new CompositeKey(user.getId(), book.getId());
        wishlistRepository.deleteById(compositeKey);
        return !wishlistRepository.existsById(compositeKey);
    }

    public List<BookResponse> getAllWishlistBooks(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        List<Wishlist> wishlists = wishlistRepository.findByUserId(user.getId());
        return wishlists.stream().map(this::getBookResponse).collect(Collectors.toList());
    }

    public BookResponse getBookResponse(Wishlist wishlist) {
        return BookResponse.builder()
                .id(wishlist.getBook().getId())
                .title(wishlist.getBook().getTitle())
                .author(wishlist.getBook().getAuthor())
                .description(wishlist.getBook().getDescription())
                .genres(wishlist.getBook().getGenres().stream().map(Genre::getGenre).collect(Collectors.toList()))
                .quality(wishlist.getBook().getQuality().getQuality())
                .status(wishlist.getBook().getStatus().getStatus())
                .language(wishlist.getBook().getLanguage().getLanguage())
                .photo(wishlist.getBook().getPhoto())
                .build();
    }
}
