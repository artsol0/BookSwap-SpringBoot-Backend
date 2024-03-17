package com.artsolo.bookswap.controllers;

import com.artsolo.bookswap.services.AuthenticationService;
import com.artsolo.bookswap.services.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            if (request.getNickname() != null && request.getEmail() != null && request.getPassword() != null) {
                return ResponseEntity.ok(authenticationService.register(request));
            }
            return ResponseEntity.badRequest().body("Invalid data");
        } catch (Exception e) {
            logger.error("Error occurred during registration", e);
            return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/confirm")
    public String confirmEmail(@RequestParam("token") String token) {
        return authenticationService.confirmEmail(token);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            if (request.getEmail() != null && request.getPassword() != null) {
                return ResponseEntity.ok(authenticationService.authenticate(request));
            }
            return ResponseEntity.badRequest().body(AuthenticationResponse.builder()
                    .message("Invalid data")
                    .build());
        } catch (Exception e) {
            logger.error("Error occurred during authentication", e);
            return new ResponseEntity<AuthenticationResponse>(AuthenticationResponse.builder()
                    .message("Something went wrong")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
