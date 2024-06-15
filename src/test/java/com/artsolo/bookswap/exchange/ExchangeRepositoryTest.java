package com.artsolo.bookswap.exchange;

import com.artsolo.bookswap.book.Book;
import com.artsolo.bookswap.user.User;
import com.artsolo.bookswap.book.BookRepository;
import com.artsolo.bookswap.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ExchangeRepositoryTest {

    @Autowired
    private ExchangeRepository exchangeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private final User firstUser = new User();
    private final User secondUser = new User();
    private final User thirdUser = new User();

    private final Book firstBook = Book.builder().owner(firstUser).build();
    private final Book secondBook = Book.builder().owner(secondUser).build();

    private final Exchange firstExchange = Exchange.builder().book(firstBook).initiator(secondUser).recipient(firstUser).build();
    private final Exchange secondExchange = Exchange.builder().book(secondBook).initiator(thirdUser).recipient(secondUser).build();
    private final Exchange thirdExchange = Exchange.builder().book(secondBook).initiator(firstUser).recipient(secondUser).build();

    @BeforeEach
    void setUp() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        userRepository.save(thirdUser);

        bookRepository.save(firstBook);
        bookRepository.save(secondBook);

        exchangeRepository.save(firstExchange);
        exchangeRepository.save(secondExchange);
        exchangeRepository.save(thirdExchange);
    }

    @AfterEach
    void tearDown() {
        exchangeRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void foundAllFirstUserInitiations() {
        List<Exchange> exchanges = exchangeRepository.findAllByInitiatorId(firstUser.getId());
        assertThat(exchanges.size()).isEqualTo(1);
        exchanges.forEach(exchange -> assertThat(exchange.getInitiator().getId()).isEqualTo(firstUser.getId()));
    }

    @Test
    void foundAllSecondUserRecipientExchanges() {
        List<Exchange> exchanges = exchangeRepository.findAllByRecipientId(secondUser.getId());
        assertThat(exchanges.size()).isEqualTo(2);
        exchanges.forEach(exchange -> assertThat(exchange.getRecipient().getId()).isEqualTo(secondUser.getId()));
    }

    @Test
    void foundAllExchangesByBookId() {
        List<Exchange> exchanges = exchangeRepository.findAllByBookId(secondBook.getId());
        assertThat(exchanges.size()).isEqualTo(2);
        exchanges.forEach(exchange -> assertThat(exchange.getBook().getId()).isEqualTo(secondBook.getId()));
    }
}