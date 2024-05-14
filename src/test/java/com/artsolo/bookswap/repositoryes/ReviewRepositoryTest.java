package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.models.Book;
import com.artsolo.bookswap.models.CompositeKey;
import com.artsolo.bookswap.models.Review;
import com.artsolo.bookswap.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    private final User firstUser = User.builder().build();
    private final User secondUser = User.builder().build();

    private final Book firstBook = Book.builder().build();
    private final Book secondBook = Book.builder().build();

    @BeforeEach
    void setUp() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        bookRepository.save(firstBook);
        bookRepository.save(secondBook);

        reviewRepository.save(
                Review.builder()
                .reviewId(new CompositeKey(firstUser.getId(), firstBook.getId()))
                .book(firstBook).user(firstUser)
                .build()
        );

        reviewRepository.save(
                Review.builder()
                .reviewId(new CompositeKey(secondUser.getId(), firstBook.getId()))
                .book(firstBook).user(secondUser)
                .build()
        );

        reviewRepository.save(
                Review.builder()
                .reviewId(new CompositeKey(firstUser.getId(), secondBook.getId()))
                .book(secondBook).user(firstUser)
                .build()
        );

    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void foundedAllFirstBookReviews() {
        Page<Review> reviews = reviewRepository.findByBookId(firstBook.getId(), PageRequest.of(0, 10));
        assertThat(reviews.getTotalPages()).isEqualTo(1);
        assertThat(reviews.getTotalElements()).isEqualTo(2);
        reviews.forEach(review -> assertThat(review.getBook().getId()).isEqualTo(firstBook.getId()));
    }

    @Test
    void foundedAllSecondBookReviews() {
        Page<Review> reviews = reviewRepository.findByBookId(secondBook.getId(), PageRequest.of(0, 10));
        assertThat(reviews.getTotalPages()).isEqualTo(1);
        assertThat(reviews.getTotalElements()).isEqualTo(1);
        reviews.forEach(review -> assertThat(review.getBook().getId()).isEqualTo(secondBook.getId()));
    }
}