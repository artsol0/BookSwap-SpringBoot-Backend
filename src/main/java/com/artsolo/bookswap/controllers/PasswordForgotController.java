package com.artsolo.bookswap.controllers;

import com.artsolo.bookswap.services.AuthenticationService;
import com.artsolo.bookswap.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/v1/forgot-password")
public class PasswordForgotController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public PasswordForgotController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(authenticationService.forgotPassword(request));
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
    public String updatePassword(@RequestParam("token") String token, @RequestParam("password") String newPassword) {
        if (authenticationService.resetPassword(token, newPassword)) {
            return "password-rest";
        }
        return "password-not-rest";
    }
}
