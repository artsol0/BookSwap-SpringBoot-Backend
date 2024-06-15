package com.artsolo.bookswap.book;

import com.artsolo.bookswap.user.User;
import com.artsolo.bookswap.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import static org.assertj.core.api.AssertionsForClassTypes.*;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    private final User firstUser = new User();
    private final User secondUser = new User();
    private final User thirdUser = new User();

    @BeforeEach
    void setUp() {

        userRepository.save(firstUser);
        userRepository.save(secondUser);
        userRepository.save(thirdUser);

        for (int i = 0; i < 6; i++) {
            Book book = Book.builder()
                    .title("My First New Book")
                    .author("First Author")
                    .owner(firstUser)
                    .build();
            bookRepository.save(book);
        }

        for (int i = 0; i < 6; i++) {
            Book book = Book.builder()
                    .title("My Second New Book")
                    .author("Second Author")
                    .owner(secondUser)
                    .build();
            bookRepository.save(book);
        }
    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void foundAllBooks() {
        Page<Book> books = bookRepository.findByTitleOrAuthorContaining(
                PageRequest.of(0, 10),
                ""
        );
        assertThat(books.getTotalPages()).isEqualTo(2);
        assertThat(books.getTotalElements()).isEqualTo(12);
    }

    @Test
    void booksFoundByTitleOrAuthorContaining() {
        Page<Book> books = bookRepository.findByTitleOrAuthorContaining(
                PageRequest.of(0, 10),
                "First"
        );
        assertThat(books.getTotalPages()).isEqualTo(1);
        assertThat(books.getTotalElements()).isEqualTo(6);
    }

    @Test
    void booksFoundByTitleContaining() {
        Page<Book> books = bookRepository.findByTitleOrAuthorContaining(
                PageRequest.of(0, 10),
                "My Second New Book"
        );
        assertThat(books.getTotalPages()).isEqualTo(1);
        assertThat(books.getTotalElements()).isEqualTo(6);
        books.forEach(book -> assertThat(book.getTitle()).containsIgnoringCase("My Second New Book"));
    }

    @Test
    void  booksFoundByAuthorContaining() {
        Page<Book> books = bookRepository.findByTitleOrAuthorContaining(
                PageRequest.of(0, 10),
                "First Author"
        );
        assertThat(books.getTotalPages()).isEqualTo(1);
        assertThat(books.getTotalElements()).isEqualTo(6);
        books.forEach(book -> assertThat(book.getAuthor()).containsIgnoringCase("First Author"));
    }

    @Test
    void noBooksFoundByTitleOrAuthorContaining() {
        Page<Book> books = bookRepository.findByTitleOrAuthorContaining(
                PageRequest.of(0, 10),
                "Some keyword"
        );
        assertThat(books.getTotalPages()).isEqualTo(0);
        assertThat(books.getTotalElements()).isEqualTo(0);
    }

    @Test
    void booksFoundByOwner() {
        Page<Book> books = bookRepository.findByOwner(PageRequest.of(0, 10), firstUser);
        assertThat(books.getTotalPages()).isEqualTo(1);
        assertThat(books.getTotalElements()).isEqualTo(6);
        books.forEach(book -> assertThat(book.getOwner().getId()).isEqualTo(firstUser.getId()));
    }

    @Test
    void noBooksFoundByOwner() {
        Page<Book> books = bookRepository.findByOwner(PageRequest.of(0, 10), thirdUser);
        assertThat(books.getTotalPages()).isEqualTo(0);
        assertThat(books.getTotalElements()).isEqualTo(0);
    }

}