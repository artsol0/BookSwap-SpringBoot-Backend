package com.artsolo.bookswap.controllers.exchange;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.models.Book;
import com.artsolo.bookswap.models.Exchange;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.services.BookService;
import com.artsolo.bookswap.services.ExchangeService;
import com.artsolo.bookswap.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/exchange")
public class ExchangeController {
    private final ExchangeService exchangeService;
    private final UserService userService;
    private final BookService bookService;

    public ExchangeController(ExchangeService exchangeService, UserService userService, BookService bookService) {
        this.exchangeService = exchangeService;
        this.userService = userService;
        this.bookService = bookService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewExchange(@RequestBody Map<String,String> request, Principal currentUser) {
        try {
            if (request.get("recipientId") != null && request.get("bookId") != null) {
                Optional<User> recipient = userService.getUserById(Long.parseLong(request.get("recipientId")));
                if (recipient.isPresent()) {
                    Optional<Book> book = bookService.getBookById(Long.parseLong(request.get("bookId")));
                    if (book.isPresent()) {
                        User initiator = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
                        if (initiator.getPoints() >= 20) {
                            if (exchangeService.createNewExchange(recipient.get(), initiator, book.get())) {
                                return ResponseEntity.ok().body(MessageResponse.builder().message("Exchange was created successfully")
                                        .build());
                            }
                            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                                    HttpStatus.BAD_REQUEST.value(), "Failed to create new exchange")).build());
                        }
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(new ErrorDescription(
                                HttpStatus.NOT_FOUND.value(), "You don't have enough points"))
                                .build());
                    }
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(new ErrorDescription(
                            HttpStatus.NOT_FOUND.value(), "Book with id '" + request.get("bookId") + "' not found"))
                            .build());
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(new ErrorDescription(
                        HttpStatus.NOT_FOUND.value(), "User with id '" + request.get("userId") + "' not found"))
                        .build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteExchangeById(@PathVariable Long id, Principal currentUser) {
        try {
            Optional<Exchange> exchange = exchangeService.getExchangeById(id);
            if (exchange.isPresent()) {
                User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
                if (exchangeService.userIsParticipantOfExchange(exchange.get(), user)){
                    if (exchangeService.deleteExchange(exchange.get())) {
                        return ResponseEntity.ok().body(MessageResponse.builder().message("Exchange was deleted successfully")
                                .build());
                    }
                    return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                            HttpStatus.BAD_REQUEST.value(), "Exchange still exist")).build());
                }
                return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                        HttpStatus.BAD_REQUEST.value(), "You are not participant of exchange")).build());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.NOT_FOUND.value(), "Exchange with id '" + id + "' not found")).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getExchangeById(@PathVariable Long id) {
        try {
            Optional<Exchange> exchange = exchangeService.getExchangeById(id);
            if (exchange.isPresent()) {
                ExchangeResponse exchangeResponse = exchangeService.getExchangeResponse(exchange.get());
                return ResponseEntity.ok().body(SuccessResponse.builder().data(exchangeResponse).build());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.NOT_FOUND.value(), "Exchange with id '" + id + "' not found")).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @GetMapping("/get/initiation")
    public ResponseEntity<?> getAllInitiateExchanges(Principal currentUser) {
        try {
            return ResponseEntity.ok().body(SuccessResponse.builder()
                    .data(exchangeService.getAllUserInitiateExchanges(currentUser)).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @GetMapping("/get/recipient")
    public ResponseEntity<?> getAllRecipientExchanges(Principal currentUser) {
        try {
            return ResponseEntity.ok().body(SuccessResponse.builder()
                    .data(exchangeService.getAllUserRecipientExchanges(currentUser)).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @PutMapping("/confirm/{id}")
    public ResponseEntity<?> confirmExchangeById(@PathVariable Long id, Principal currentUser) {
        try {
            Optional<Exchange> exchange = exchangeService.getExchangeById(id);
            if (exchange.isPresent()) {
                User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
                if (exchangeService.userIsRecipientOfExchange(exchange.get(), user)) {
                    if (exchangeService.confirmExchange(exchange.get(), user)) {
                        return ResponseEntity.ok().body(MessageResponse.builder().message("Exchange was confirmed successfully")
                                .build());
                    }
                    return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                            HttpStatus.BAD_REQUEST.value(), "Failed to confirm exchange")).build());
                }
                return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                        HttpStatus.BAD_REQUEST.value(), "You are not recipient of exchange")).build());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.NOT_FOUND.value(), "Exchange with id '" + id + "' not found")).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }



}
