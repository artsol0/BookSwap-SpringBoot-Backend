package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.exchange.ExchangeResponse;
import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.models.*;
import com.artsolo.bookswap.repositoryes.ExchangeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final ExchangeRepository exchangeRepository;
    private final LibraryService libraryService;
    private final UserService userService;

    public boolean createNewExchange(User initiator, Book book) {
        Library library = libraryService.getLibraryByBookId(book.getId());
        Exchange exchange = Exchange.builder()
                .initiator(initiator)
                .recipient(library.getUser())
                .book(book)
                .confirmed(Boolean.FALSE)
                .build();
        exchange = exchangeRepository.save(exchange);
        return exchangeRepository.existsById(exchange.getId());
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

    public List<ExchangeResponse> getAllUserInitiateExchanges(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        List<ExchangeResponse> exchangeResponses = new ArrayList<>();
        List<Exchange> exchanges = exchangeRepository.findAllByInitiatorId(user.getId());
        for (Exchange exchange : exchanges) {
            exchangeResponses.add(getExchangeResponse(exchange));
        }
        return exchangeResponses;
    }

    public List<ExchangeResponse> getAllUserRecipientExchanges(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        List<ExchangeResponse> exchangeResponses = new ArrayList<>();
        List<Exchange> exchanges = exchangeRepository.findAllByRecipientId(user.getId());
        for (Exchange exchange : exchanges) {
            exchangeResponses.add(getExchangeResponse(exchange));
        }
        return exchangeResponses;
    }

    public boolean deleteExchange(Exchange exchange) {
        exchangeRepository.deleteById(exchange.getId());
        return !exchangeRepository.existsById(exchange.getId());
    }

    @Transactional
    public boolean confirmExchange(Exchange exchange, Library library) {
        if (libraryService.removeBookFromUserLibrary(library)) {
            if (libraryService.addNewBookToUserLibrary(exchange.getInitiator(), exchange.getBook())) {
                exchange.setConfirmed(Boolean.TRUE);
                exchange = exchangeRepository.save(exchange);
                return exchange.getConfirmed();
            }
        }
        return false;
    }

    public boolean exchangeIsConfirmed(Exchange exchange) {
        return exchange.getConfirmed();
    }

    public boolean userIsRecipientOfExchange(Exchange exchange, User providedRecipient) {
        User actualRecipient = exchange.getRecipient();
        return actualRecipient.getId().equals(providedRecipient.getId());
    }

    public boolean userIsParticipantOfExchange(Exchange exchange, User user) {
        User recipient = exchange.getRecipient();
        User initiator = exchange.getInitiator();
        return (user.getId().equals(recipient.getId()) || user.getId().equals(initiator.getId()));
    }
}
