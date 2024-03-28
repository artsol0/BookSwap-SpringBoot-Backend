package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.GetExchangeResponse;
import com.artsolo.bookswap.models.*;
import com.artsolo.bookswap.repositoryes.BookRepository;
import com.artsolo.bookswap.repositoryes.ExchangeRepository;
import com.artsolo.bookswap.repositoryes.LibraryRepository;
import com.artsolo.bookswap.repositoryes.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExchangeService {
    private final ExchangeRepository exchangeRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LibraryService libraryService;
    private final UserService userService;

    public ExchangeService(ExchangeRepository exchangeRepository, UserRepository userRepository, BookRepository bookRepository, LibraryService libraryService, UserService userService) {
        this.exchangeRepository = exchangeRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.libraryService = libraryService;
        this.userService = userService;
    }

    public boolean createNewExchange(Map<String,String> request, Principal currentUser) {
        User initiator = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        if (initiator.getPoints() >= 20) {
            User recipient = userRepository.findById(Long.parseLong(request.get("recipientId"))).orElse(null);
            Book book = bookRepository.findById(Long.parseLong(request.get("bookId"))).orElse(null);
            if (recipient != null && book != null) {
                Exchange exchange = Exchange.builder()
                        .initiator(initiator)
                        .recipient(recipient)
                        .book(book)
                        .confirmed(Boolean.FALSE)
                        .build();
                exchange = exchangeRepository.save(exchange);
                return exchangeRepository.existsById(exchange.getId());
            }
        }
        return false;
    }

    public Exchange getExchangeById(Long id) {return exchangeRepository.findById(id).orElse(null);}

    public List<GetExchangeResponse> getAllUserInitiateExchanges(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        List<GetExchangeResponse> exchangeResponses = new ArrayList<>();
        List<Exchange> exchanges = exchangeRepository.findAllByInitiatorId(user.getId());
        for (Exchange exchange : exchanges) {
            exchangeResponses.add(GetExchangeResponse.builder()
                            .id(exchange.getId())
                            .initiator(exchange.getInitiator().getNickname())
                            .recipient(exchange.getRecipient().getNickname())
                            .book(exchange.getBook().getTitle())
                            .confirmed(exchange.getConfirmed())
                            .build());
        }
        return exchangeResponses;
    }

    public List<GetExchangeResponse> getAllUserRecipientExchanges(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        List<GetExchangeResponse> exchangeResponses = new ArrayList<>();
        List<Exchange> exchanges = exchangeRepository.findAllByRecipientId(user.getId());
        for (Exchange exchange : exchanges) {
            exchangeResponses.add(GetExchangeResponse.builder()
                    .id(exchange.getId())
                    .initiator(exchange.getInitiator().getNickname())
                    .recipient(exchange.getRecipient().getNickname())
                    .book(exchange.getBook().getTitle())
                    .confirmed(exchange.getConfirmed())
                    .build());
        }
        return exchangeResponses;
    }

    public boolean deleteExchangeById(Long id) {
        Optional<Exchange> exchange = exchangeRepository.findById(id);
        if (exchange.isPresent()) {
            exchangeRepository.deleteById(exchange.get().getId());
            return !exchangeRepository.existsById(exchange.get().getId());
        }
        return false;
    }

    @Transactional
    public boolean confirmExchangeById(Long id, Principal currentUser) {
        Optional<Exchange> exchange = exchangeRepository.findById(id);
        if (exchange.isPresent()) {
            User recipient = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
            if (userIsRecipientOfExchange(exchange.get(), recipient)) {
                User initiator = exchange.get().getInitiator();
                if (libraryService.removeBookFromUserLibrary(recipient, exchange.get().getBook())) {
                    userService.increaseUserPoints(15, recipient);
                    if (libraryService.addNewBookToUserLibrary(initiator, exchange.get().getBook())) {
                        userService.decreaseUserPoints(20, initiator);
                        exchange.get().setConfirmed(Boolean.TRUE);
                        return exchange.get().getConfirmed();
                    }
                }
            }
        }
        return false;
    }

    public boolean userIsRecipientOfExchange(Exchange exchange, User providedRecipient) {
        User actualRecipient = exchange.getRecipient();
        return actualRecipient.getId().equals(providedRecipient.getId());
    }
}
