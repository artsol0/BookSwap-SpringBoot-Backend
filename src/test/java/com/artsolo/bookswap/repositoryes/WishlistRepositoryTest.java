package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.book.Book;
import com.artsolo.bookswap.book.BookRepository;
import com.artsolo.bookswap.compositekey.CompositeKey;
import com.artsolo.bookswap.user.User;
import com.artsolo.bookswap.wishlist.Wishlist;
import com.artsolo.bookswap.user.UserRepository;
import com.artsolo.bookswap.wishlist.WishlistRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

@DataJpaTest
class WishlistRepositoryTest {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    private final User firstUser = new User();
    private final User secondUser = new User();
    private final User thirdUser = new User();

    private final Book firstBook = new Book();
    private final Book secondBook = new Book();

    @BeforeEach
    void setUp() {

        userRepository.save(firstUser);
        userRepository.save(secondUser);
        userRepository.save(thirdUser);

        bookRepository.save(firstBook);
        bookRepository.save(secondBook);

        Wishlist firstUserWishlist = new Wishlist(
                new CompositeKey(firstUser.getId(), firstBook.getId()), firstUser, firstBook);
        wishlistRepository.save(firstUserWishlist);

        firstUserWishlist = new Wishlist(
                new CompositeKey(firstUser.getId(), secondBook.getId()), firstUser, secondBook);
        wishlistRepository.save(firstUserWishlist);

        Wishlist secondUserWishlist = new Wishlist(
                new CompositeKey(secondUser.getId(), firstBook.getId()), secondUser, firstBook);
        wishlistRepository.save(secondUserWishlist);
    }

    @AfterEach
    void tearDown() {
        wishlistRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void foundAllFirstUserWishlistBooks() {
        List<Wishlist> wishlist = wishlistRepository.findByUser(firstUser);
        assertThat(wishlist.size()).isEqualTo(2);
        wishlist.forEach(wish -> assertThat(wish.getUser().getId()).isEqualTo(firstUser.getId()));
    }

    @Test
    void foundAllSecondUserWishlistBooks() {
        List<Wishlist> wishlist = wishlistRepository.findByUser(secondUser);
        assertThat(wishlist.size()).isEqualTo(1);
        wishlist.forEach(wish -> assertThat(wish.getUser().getId()).isEqualTo(secondUser.getId()));
    }

    @Test
    void thirdUserWishlistIsEmpty() {
        List<Wishlist> wishlist = wishlistRepository.findByUser(thirdUser);
        assertThat(wishlist.size()).isEqualTo(0);
    }
}