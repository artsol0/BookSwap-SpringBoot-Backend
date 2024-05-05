package com.artsolo.bookswap.controllers.exchange;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.models.*;
import com.artsolo.bookswap.services.BookService;
import com.artsolo.bookswap.services.ExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@Slf4j
@RequestMapping("/api/v1/exchange")
public class ExchangeController {
    private final ExchangeService exchangeService;
    private final BookService bookService;

    public ExchangeController(ExchangeService exchangeService, BookService bookService) {
        this.exchangeService = exchangeService;
        this.bookService = bookService;
    }

    @PostMapping("/create/{bookId}")
    public ResponseEntity<?> createNewExchange(@PathVariable Long bookId, Principal currentUser) {
        Book book = bookService.getBookById(bookId);
        User initiator = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        if (initiator.getPoints() >= 20) {
            if (exchangeService.createNewExchange(initiator, book)) {
                return ResponseEntity.ok().body(MessageResponse.builder().message("Exchange was created successfully").build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Failed to create new exchange")).build());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.NOT_FOUND.value(), "You don't have enough points")).build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteExchangeById(@PathVariable Long id, Principal currentUser) {
        Exchange exchange = exchangeService.getExchangeById(id);
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        if (exchangeService.userIsParticipantOfExchange(exchange, user)){
            if (exchangeService.deleteExchange(exchange)) {
                return ResponseEntity.ok().body(MessageResponse.builder().message("Exchange was deleted successfully")
                        .build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Exchange still exist")).build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "You are not participant of exchange")).build());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getExchangeById(@PathVariable Long id) {
        ExchangeResponse exchangeResponse = exchangeService.getExchangeResponse(exchangeService.getExchangeById(id));
        return ResponseEntity.ok().body(SuccessResponse.builder().data(exchangeResponse).build());
    }

    @GetMapping("/get/initiation")
    public ResponseEntity<?> getAllInitiateExchanges(Principal currentUser) {
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .data(exchangeService.getAllUserInitiateExchanges(currentUser)).build());
    }

    @GetMapping("/get/recipient")
    public ResponseEntity<?> getAllRecipientExchanges(Principal currentUser) {
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .data(exchangeService.getAllUserRecipientExchanges(currentUser)).build());
    }

    @PutMapping("/confirm/{id}")
    public ResponseEntity<?> confirmExchangeById(@PathVariable Long id, Principal currentUser) {
        Exchange exchange = exchangeService.getExchangeById(id);
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        if (!exchangeService.exchangeIsConfirmed(exchange)) {
            if (exchangeService.userIsRecipientOfExchange(exchange, user)) {
                if (exchangeService.confirmExchange(exchange)) {
                    return ResponseEntity.ok().body(MessageResponse.builder().message("Exchange was confirmed successfully")
                            .build());
                }
                return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                        HttpStatus.BAD_REQUEST.value(), "Failed to confirm exchange")).build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "You are not recipient of exchange")).build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Exchange is already confirmed")).build());
    }



}
