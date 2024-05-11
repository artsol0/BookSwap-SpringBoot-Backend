package com.artsolo.bookswap.controllers.auth;

import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.services.AuthenticationService;
import com.artsolo.bookswap.services.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/forgot-password")
@RequiredArgsConstructor
public class PasswordForgotController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<MessageResponse> forgotPassword(@RequestParam("email") String email) {
        return ResponseEntity.ok().body(MessageResponse.builder().message(authenticationService.forgotPassword(email)).build());
    }

    @GetMapping("/check-token")
    public boolean resetPassword(@RequestParam("token") String token) {
        return jwtService.isTokenExpired(token);
    }

    @PutMapping("/reset-password")
    public boolean updatePassword(@RequestParam("token") String token, @RequestBody @Valid ResetPasswordRequest request) {
        return authenticationService.resetPassword(token, request.getNewPassword());
    }
}
