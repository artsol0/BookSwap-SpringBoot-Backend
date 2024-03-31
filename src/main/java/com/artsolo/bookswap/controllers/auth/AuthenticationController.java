package com.artsolo.bookswap.controllers.auth;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.services.AuthenticationService;
import com.artsolo.bookswap.services.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            if (request.getNickname() != null && request.getEmail() != null && request.getPassword() != null) {
                if (authenticationService.register(request)) {
                    return ResponseEntity.ok().body(MessageResponse.builder().message("User registered successfully")
                            .build());
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.builder().error(new ErrorDescription(
                        HttpStatus.CONFLICT.value(), "Email address is already taken")).build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
        } catch (Exception e) {
            logger.error("Error occurred during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }

    @GetMapping("/confirm")
    public String confirmEmail(@RequestParam("token") String token) {
        return authenticationService.confirmEmail(token);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            if (request.getEmail() != null && request.getPassword() != null) {
                AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
                if (authenticationResponse.getToken() != null && !authenticationResponse.getToken().isEmpty()) {
                    return ResponseEntity.ok().body(SuccessResponse.builder().data(authenticationResponse).build());
                }
                return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                        HttpStatus.BAD_REQUEST.value(), "Invalid data credentials")).build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
        } catch (Exception e) {
            logger.error("Error occurred during authentication", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                    .error(new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"))
                    .build()
            );
        }
    }
}
