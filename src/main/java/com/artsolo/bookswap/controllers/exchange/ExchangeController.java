package com.artsolo.bookswap.controllers.exchange;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.models.Exchange;
import com.artsolo.bookswap.services.ExchangeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/exchange")
public class ExchangeController {
    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewExchange(@RequestBody Map<String,String> request, Principal currentUser) {
        try {
            if (request.get("recipientId") != null && request.get("bookId") != null) {
                if (exchangeService.createNewExchange(request, currentUser)) {
                    return ResponseEntity.ok().body(new MessageResponse("Exchange was created successfully"));
                }
                return ResponseEntity.badRequest().body(new ErrorResponse(new ErrorDescription(
                        HttpStatus.BAD_REQUEST.value(),
                        "Failed to create new exchange")
                ));
            }
            return ResponseEntity.badRequest().body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad request")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteExchangeById(@PathVariable Long id) {
        try {
            if (exchangeService.deleteExchangeById(id)) {
                return ResponseEntity.ok().body(new MessageResponse("Exchange was deleted successfully"));
            }
            return ResponseEntity.badRequest().body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(),
                    "Exchange still exits")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getExchangeById(@PathVariable Long id) {
        try {
            Exchange exchange = exchangeService.getExchangeById(id);
            if (exchange != null) {
                return ResponseEntity.ok().body(new SuccessResponse<>(GetExchangeResponse.builder()
                        .id(exchange.getId())
                        .initiator(exchange.getInitiator().getNickname())
                        .recipient(exchange.getRecipient().getNickname())
                        .book(exchange.getBook().getTitle())
                        .confirmed(exchange.getConfirmed())
                        .build()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.NOT_FOUND.value(),
                    "Exchange with id '" + id + "' not found")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }

    @GetMapping("/get/initiation")
    public ResponseEntity<?> getAllInitiateExchanges(Principal currentUser) {
        try {
            return ResponseEntity.ok().body(new SuccessResponse<>(exchangeService.getAllUserInitiateExchanges(currentUser)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }

    @GetMapping("/get/recipient")
    public ResponseEntity<?> getAllRecipientExchanges(Principal currentUser) {
        try {
            return ResponseEntity.ok().body(new SuccessResponse<>(exchangeService.getAllUserRecipientExchanges(currentUser)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }

    @PutMapping("/confirm/{id}")
    public ResponseEntity<?> confirmExchangeById(@PathVariable Long id, Principal currentUser) {
        try {
            if (exchangeService.confirmExchangeById(id, currentUser)) {
                return ResponseEntity.ok().body(new MessageResponse("Exchange was confirmed successfully"));
            }
            return ResponseEntity.badRequest().body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(),
                    "Exchange with id '" + id + "' not found")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }



}
