package com.artsolo.bookswap.exchange;

import com.artsolo.bookswap.book.Book;
import com.artsolo.bookswap.responses.ErrorDescription;
import com.artsolo.bookswap.responses.ErrorResponse;
import com.artsolo.bookswap.responses.MessageResponse;
import com.artsolo.bookswap.responses.SuccessResponse;
import com.artsolo.bookswap.book.BookService;
import com.artsolo.bookswap.exchange.dto.ExchangeResponse;
import com.artsolo.bookswap.user.UserService;
import com.artsolo.bookswap.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exchange")
public class ExchangeController {

    private final ExchangeService exchangeService;
    private final BookService bookService;
    private final UserService userService;

    @PostMapping("/create/{bookId}")
    public ResponseEntity<?> createNewExchange(@PathVariable Long bookId, Principal currentUser) {
        Book book = bookService.getBookById(bookId);
        User initiator = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        if (initiator.getPoints() >= 20) {
            exchangeService.createNewExchange(initiator, book);
            userService.decreaseUserPoints(20, initiator);
            return ResponseEntity.ok().body(MessageResponse.builder().message("Exchange was created successfully").build());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.NOT_FOUND.value(), "You don't have enough points")).build());
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<?> deleteExchangeById(@PathVariable Long id, Principal currentUser) {
        Exchange exchange = exchangeService.getExchangeById(id);
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        if (exchangeService.isUserParticipantOfExchange(exchange, user)){
            if (exchangeService.deleteExchange(exchange)) {
                if (!exchangeService.isExchangeConfirmed(exchange)) {
                    userService.increaseUserPoints(20, exchange.getInitiator());
                }
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
    public ResponseEntity<SuccessResponse<ExchangeResponse>> getExchangeById(@PathVariable Long id) {
        ExchangeResponse exchangeResponse = exchangeService.getExchangeResponse(exchangeService.getExchangeById(id));
        return ResponseEntity.ok().body(new SuccessResponse<>(exchangeResponse));
    }

    @GetMapping("/get/initiation")
    public ResponseEntity<SuccessResponse<List<ExchangeResponse>>> getAllInitiateExchanges(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        return ResponseEntity.ok().body(new SuccessResponse<>(exchangeService.getAllUserInitiateExchanges(user)));
    }

    @GetMapping("/get/recipient")
    public ResponseEntity<SuccessResponse<List<ExchangeResponse>>> getAllRecipientExchanges(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        return ResponseEntity.ok().body(new SuccessResponse<>(exchangeService.getAllUserRecipientExchanges(user)));
    }

    @PutMapping("/confirm/{id}")
    @Transactional
    public ResponseEntity<?> confirmExchangeById(@PathVariable Long id, Principal currentUser) {
        Exchange exchange = exchangeService.getExchangeById(id);
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        if (!exchangeService.isExchangeConfirmed(exchange)) {
            if (exchangeService.isUserRecipientOfExchange(exchange, user)) {
                if (exchangeService.confirmExchange(exchange)) {
                    userService.increaseUserPoints(25, exchange.getRecipient());
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
