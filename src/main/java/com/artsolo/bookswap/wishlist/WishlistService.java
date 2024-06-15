package com.artsolo.bookswap.wishlist;

import com.artsolo.bookswap.attributes.genre.Genre;
import com.artsolo.bookswap.book.Book;
import com.artsolo.bookswap.book.dto.BookResponse;
import com.artsolo.bookswap.compositekey.*;
import com.artsolo.bookswap.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    public WishlistService(WishlistRepository wishlistRepository) {this.wishlistRepository = wishlistRepository;}

    public void addBookToWishlist(Book book, User user) {
        CompositeKey compositeKey = new CompositeKey(user.getId(), book.getId());
        Wishlist wishlist = new Wishlist(compositeKey, user, book);
        wishlistRepository.save(wishlist);
    }

    public boolean removeBookFromWishlist(Book book, User user) {
        CompositeKey compositeKey = new CompositeKey(user.getId(), book.getId());
        wishlistRepository.deleteById(compositeKey);
        return !wishlistRepository.existsById(compositeKey);
    }

    public List<BookResponse> getAllWishlistBooks(User user) {
        List<Wishlist> wishlists = wishlistRepository.findByUser(user);
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
