package com.artsolo.bookswap.controllers.auth;

import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.services.AuthenticationService;
import com.artsolo.bookswap.services.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/v1/forgot-password")
@RequiredArgsConstructor
public class PasswordForgotController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok().body(MessageResponse.builder().message(authenticationService.forgotPassword(request)).build());
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token, Model model) {
        if (!jwtService.isTokenExpired(token)) {
            model.addAttribute("token", token);
            return "reset-password-form";
        }
        return "Token has been expired";
    }

    @PostMapping("/reset-password")
    public String updatePassword(@RequestParam("token") String token, @Valid ResetPasswordRequest request) {
        if (authenticationService.resetPassword(token, request.getNewPassword())) {
            return "password-reset";
        }
        return "password-not-reset";
    }
}
