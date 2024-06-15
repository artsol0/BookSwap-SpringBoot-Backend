package com.artsolo.bookswap.exchange;

import com.artsolo.bookswap.book.Book;
import com.artsolo.bookswap.book.BookService;
import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.exchange.dto.ExchangeResponse;
import com.artsolo.bookswap.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;
    private final BookService bookService;

    public void createNewExchange(User initiator, Book book) {
        Exchange exchange = Exchange.builder()
                .initiator(initiator)
                .recipient(book.getOwner())
                .book(book)
                .confirmed(Boolean.FALSE)
                .build();
        exchangeRepository.save(exchange);
    }

    public Exchange getExchangeById(Long id) {
        return exchangeRepository.findById(id).orElseThrow(() -> new NoDataFoundException("Exchange", id));
    }

    public ExchangeResponse getExchangeResponse(Exchange exchange) {
        return ExchangeResponse.builder()
                .id(exchange.getId())
                .initiatorId(exchange.getInitiator().getId())
                .initiator(exchange.getInitiator().getNickname())
                .recipientId(exchange.getRecipient().getId())
                .recipient(exchange.getRecipient().getNickname())
                .bookId(exchange.getBook().getId())
                .book(exchange.getBook().getTitle())
                .confirmed(exchange.getConfirmed())
                .build();
    }

    public List<ExchangeResponse> getAllUserInitiateExchanges(User user) {
        List<Exchange> exchanges = exchangeRepository.findAllByInitiatorId(user.getId());
        return exchanges.stream().map(this::getExchangeResponse).collect(Collectors.toList());
    }

    public List<ExchangeResponse> getAllUserRecipientExchanges(User user) {
        List<Exchange> exchanges = exchangeRepository.findAllByRecipientId(user.getId());
        return exchanges.stream().map(this::getExchangeResponse).collect(Collectors.toList());
    }

    public boolean deleteExchange(Exchange exchange) {
        exchangeRepository.deleteById(exchange.getId());
        return !exchangeRepository.existsById(exchange.getId());
    }

    @Transactional
    public boolean confirmExchange(Exchange exchange) {
        if (bookService.changeBookOwner(exchange.getBook(), exchange.getInitiator())) {
            if (changeExchangeConfirmStatus(exchange)) {
                setNewRecipientForExchanges(
                        exchangeRepository.findAllByBookId(exchange.getBook().getId()),
                        exchange.getInitiator()
                );
                return true;
            }
        }
        return false;
    }

    public boolean isExchangeConfirmed(Exchange exchange) {
        return exchange.getConfirmed();
    }

    public boolean isUserRecipientOfExchange(Exchange exchange, User providedRecipient) {
        User actualRecipient = exchange.getRecipient();
        return actualRecipient.getId().equals(providedRecipient.getId());
    }

    public boolean isUserParticipantOfExchange(Exchange exchange, User user) {
        User recipient = exchange.getRecipient();
        User initiator = exchange.getInitiator();
        return (user.getId().equals(recipient.getId()) || user.getId().equals(initiator.getId()));
    }

    public boolean changeExchangeConfirmStatus(Exchange exchange) {
        exchange.setConfirmed(!exchange.getConfirmed());
        exchange = exchangeRepository.save(exchange);
        return exchange.getConfirmed();
    }

    public void setNewRecipientForExchanges(List<Exchange> exchanges, User newRecipient) {
        exchanges.forEach(exchange -> {
            if (!exchange.getInitiator().getId().equals(newRecipient.getId())) {
                exchange.setRecipient(newRecipient);
                exchangeRepository.save(exchange);
            }
        });
    }
}
