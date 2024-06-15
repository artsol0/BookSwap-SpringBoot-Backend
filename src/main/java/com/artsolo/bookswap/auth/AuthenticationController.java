package com.artsolo.bookswap.auth;

import com.artsolo.bookswap.auth.dto.AuthenticationRequest;
import com.artsolo.bookswap.auth.dto.AuthenticationResponse;
import com.artsolo.bookswap.auth.dto.RegisterRequest;
import com.artsolo.bookswap.responses.ErrorDescription;
import com.artsolo.bookswap.responses.ErrorResponse;
import com.artsolo.bookswap.responses.MessageResponse;
import com.artsolo.bookswap.responses.SuccessResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        if (!authenticationService.isEmailTaken(request.getEmail())) {
                if (!authenticationService.isNicknameTaken(request.getNickname())) {
                    authenticationService.register(request);
                    return ResponseEntity.ok().body(MessageResponse.builder()
                            .message("The confirmation link was sent to your email address")
                            .build());
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.builder().error(new ErrorDescription(
                        HttpStatus.CONFLICT.value(), "Nickname is already taken")).build());
            }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.CONFLICT.value(), "Email address is already taken")).build());
    }

    @GetMapping("/confirm")
    public String confirmEmail(@RequestParam("token") String token) {
        return authenticationService.confirmEmail(token);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
        if (authenticationResponse.getToken() != null && !authenticationResponse.getToken().isEmpty()) {
            return ResponseEntity.ok().body(SuccessResponse.builder().data(authenticationResponse).build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Invalid data credentials")).build());
    }
}
