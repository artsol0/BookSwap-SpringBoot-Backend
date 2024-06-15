package com.artsolo.bookswap.exchange;

import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.book.Book;
import com.artsolo.bookswap.exchange.dto.ExchangeResponse;
import com.artsolo.bookswap.user.User;
import com.artsolo.bookswap.book.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeServiceTest {

    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private ExchangeService exchangeService;

    @Test
    void newExchangeCreated() {
        User bookOwner = User.builder().id(1L).build();
        User initiator = User.builder().id(2L).build();
        Book book = Book.builder().id(4L).owner(bookOwner).build();

        exchangeService.createNewExchange(initiator, book);
        ArgumentCaptor<Exchange> exchangeArgumentCaptor = ArgumentCaptor.forClass(Exchange.class);
        verify(exchangeRepository).save(exchangeArgumentCaptor.capture());
        Exchange capturedExchange = exchangeArgumentCaptor.getValue();

        assertThat(capturedExchange.getInitiator()).isEqualTo(initiator);
        assertThat(capturedExchange.getRecipient()).isEqualTo(bookOwner);
        assertThat(capturedExchange.getBook()).isEqualTo(book);
        assertThat(capturedExchange.getConfirmed()).isFalse();
    }

    @Test
    void exchangeFoundById() {
        Long exchangeId = 4L;
        Exchange exchange = Exchange.builder().id(exchangeId).build();
        when(exchangeRepository.findById(exchangeId)).thenReturn(Optional.of(exchange));
        Exchange result = exchangeService.getExchangeById(exchangeId);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(exchangeId);
    }

    @Test
    void thrownExceptionWhileGettingExchangeById() {
        when(exchangeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoDataFoundException.class, () -> exchangeService.getExchangeById(1L));
    }

    @Test
    void getExchangeResponseIsValid() {
        User bookOwner = User.builder().id(1L).nickname("recipient").build();
        User initiator = User.builder().id(2L).nickname("initiator").build();
        Book book = Book.builder().id(4L).title("book title").owner(bookOwner).build();

        Exchange exchange = Exchange.builder()
                .id(6L)
                .recipient(bookOwner)
                .initiator(initiator)
                .book(book)
                .confirmed(Boolean.FALSE)
                .build();

        ExchangeResponse response = exchangeService.getExchangeResponse(exchange);

        assertThat(response.getId()).isEqualTo(exchange.getId());
        assertThat(response.getInitiatorId()).isEqualTo(initiator.getId());
        assertThat(response.getInitiator()).isEqualTo(initiator.getNickname());
        assertThat(response.getRecipientId()).isEqualTo(bookOwner.getId());
        assertThat(response.getRecipient()).isEqualTo(bookOwner.getNickname());
        assertThat(response.getBook()).isEqualTo(book.getTitle());
        assertThat(response.getConfirmed()).isFalse();
    }

    @Test
    void userExchangesFoundByInitiations() {
        User bookOwner = User.builder().id(1L).nickname("recipient").build();
        User initiator = User.builder().id(2L).nickname("initiator").build();
        Book book1 = Book.builder().id(4L).title("book1 title").owner(bookOwner).build();
        Book book2 = Book.builder().id(5L).title("book2 title").owner(bookOwner).build();

        Exchange exchange1 = Exchange.builder()
                .id(6L)
                .recipient(bookOwner)
                .initiator(initiator)
                .book(book1)
                .confirmed(Boolean.FALSE)
                .build();

        Exchange exchange2 = Exchange.builder()
                .id(7L)
                .recipient(bookOwner)
                .initiator(initiator)
                .book(book2)
                .confirmed(Boolean.TRUE)
                .build();

        when(exchangeRepository.findAllByInitiatorId(initiator.getId())).thenReturn(Arrays.asList(exchange1, exchange2));
        List<ExchangeResponse> exchanges = exchangeService.getAllUserInitiateExchanges(initiator);

        assertThat(exchanges.size()).isEqualTo(2);
        exchanges.forEach(exchangeResponse -> assertThat(exchangeResponse.getInitiatorId()).isEqualTo(initiator.getId()));
    }

    @Test
    void userExchangesFoundByRecipientMembership() {
        User bookOwner = User.builder().id(1L).nickname("recipient").build();
        User initiator = User.builder().id(2L).nickname("initiator").build();
        Book book1 = Book.builder().id(4L).title("book1 title").owner(bookOwner).build();
        Book book2 = Book.builder().id(5L).title("book2 title").owner(bookOwner).build();

        Exchange exchange1 = Exchange.builder()
                .id(6L)
                .recipient(bookOwner)
                .initiator(initiator)
                .book(book1)
                .confirmed(Boolean.FALSE)
                .build();

        Exchange exchange2 = Exchange.builder()
                .id(7L)
                .recipient(bookOwner)
                .initiator(initiator)
                .book(book2)
                .confirmed(Boolean.TRUE)
                .build();

        when(exchangeRepository.findAllByRecipientId(bookOwner.getId())).thenReturn(Arrays.asList(exchange1, exchange2));
        List<ExchangeResponse> exchanges = exchangeService.getAllUserRecipientExchanges(bookOwner);

        assertThat(exchanges.size()).isEqualTo(2);
        exchanges.forEach(exchangeResponse -> assertThat(exchangeResponse.getRecipientId()).isEqualTo(bookOwner.getId()));
    }

    @Test
    void exchangeStatusWasChanged() {
        Exchange exchange = Exchange.builder().id(4L).confirmed(Boolean.FALSE).build();
        Exchange svdExchange = Exchange.builder().id(4L).confirmed(Boolean.TRUE).build();
        when(exchangeRepository.save(exchange)).thenReturn(svdExchange);

        boolean isStatusChanged = exchangeService.changeExchangeConfirmStatus(exchange);
        ArgumentCaptor<Exchange> exchangeArgumentCaptor = ArgumentCaptor.forClass(Exchange.class);
        verify(exchangeRepository).save(exchangeArgumentCaptor.capture());
        Exchange capturedExchange = exchangeArgumentCaptor.getValue();

        assertThat(isStatusChanged).isTrue();
        assertThat(capturedExchange.getConfirmed()).isTrue();
    }

    @Test
    void exchangeIsConfirmed() {
        Exchange exchange = Exchange.builder().id(4L).confirmed(Boolean.TRUE).build();
        boolean confirm = exchangeService.isExchangeConfirmed(exchange);
        assertThat(confirm).isTrue();
    }

    @Test
    void exchangeIsNotConfirmed() {
        Exchange exchange = Exchange.builder().id(4L).confirmed(Boolean.FALSE).build();
        boolean confirm = exchangeService.isExchangeConfirmed(exchange);
        assertThat(confirm).isFalse();
    }

    @Test
    void userIsRecipientOfExchange() {
        User bookOwner = User.builder().id(1L).build();
        User initiator = User.builder().id(2L).build();

        Exchange exchange = Exchange.builder()
                .recipient(bookOwner)
                .initiator(initiator)
                .build();

        boolean isRecipient = exchangeService.isUserRecipientOfExchange(exchange, bookOwner);
        assertThat(isRecipient).isTrue();
    }

    @Test
    void userIsNotRecipientOfExchange() {
        User bookOwner = User.builder().id(1L).build();
        User initiator = User.builder().id(2L).build();

        Exchange exchange = Exchange.builder()
                .recipient(bookOwner)
                .initiator(initiator)
                .build();

        boolean isRecipient = exchangeService.isUserRecipientOfExchange(exchange, initiator);
        assertThat(isRecipient).isFalse();
    }

    @Test
    void usersAreParticipantsOfExchange() {
        User bookOwner = User.builder().id(1L).build();
        User initiator = User.builder().id(2L).build();

        Exchange exchange = Exchange.builder()
                .recipient(bookOwner)
                .initiator(initiator)
                .build();

        boolean isFirstParticipant = exchangeService.isUserParticipantOfExchange(exchange, initiator);
        boolean isSecondParticipant = exchangeService.isUserParticipantOfExchange(exchange, bookOwner);

        assertThat(isFirstParticipant).isTrue();
        assertThat(isSecondParticipant).isTrue();
    }

    @Test
    void userIsNotParticipantOfExchange() {
        User bookOwner = User.builder().id(1L).build();
        User initiator = User.builder().id(2L).build();
        User user = User.builder().id(5L).build();

        Exchange exchange = Exchange.builder()
                .recipient(bookOwner)
                .initiator(initiator)
                .build();

        boolean isParticipant = exchangeService.isUserParticipantOfExchange(exchange, user);
        assertThat(isParticipant).isFalse();
    }

    @Test
    void newRecipientOfExchangesSet() {
        User bookOwner = User.builder().id(1L).build();
        User firstInitiator = User.builder().id(2L).build();
        User secondInitiator = User.builder().id(3L).build();
        User newRecipient = User.builder().id(4L).build();

        Exchange exchange1 = Exchange.builder()
                .recipient(bookOwner)
                .initiator(firstInitiator)
                .build();

        Exchange exchange2 = Exchange.builder()
                .recipient(bookOwner)
                .initiator(secondInitiator)
                .build();

        Exchange exchange3 = Exchange.builder()
                .recipient(bookOwner)
                .initiator(newRecipient)
                .build();

        List<Exchange> exchanges = Arrays.asList(exchange1, exchange2, exchange3);

        when(exchangeRepository.save(any(Exchange.class))).thenAnswer(invocation -> invocation.getArgument(0));
        exchangeService.setNewRecipientForExchanges(exchanges, newRecipient);
        verify(exchangeRepository, times(2)).save(any(Exchange.class));

        exchanges.forEach(exchange -> {
            if (!exchange.getInitiator().getId().equals(newRecipient.getId())) {
                assertThat(exchange.getRecipient()).isEqualTo(newRecipient);
            }
        });
    }

}