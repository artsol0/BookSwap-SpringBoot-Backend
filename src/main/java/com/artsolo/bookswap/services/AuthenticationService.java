package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.auth.AuthenticationRequest;
import com.artsolo.bookswap.controllers.auth.AuthenticationResponse;
import com.artsolo.bookswap.controllers.auth.RegisterRequest;
import com.artsolo.bookswap.models.enums.Role;
import com.artsolo.bookswap.models.Token;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.repositoryes.TokenRepository;
import com.artsolo.bookswap.repositoryes.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailSender emailSender;

    public AuthenticationService(UserRepository userRepository, TokenRepository tokenRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, EmailSender emailSender) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailSender = emailSender;
    }

    public void register(RegisterRequest request) throws IOException {
        var user = User.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.READER)
                .activity(Boolean.FALSE)
                .registrationDate(LocalDate.now())
                .points(0)
                .photo(Files.readAllBytes(Paths.get("./src/main/resources/static/default-avatar-icon.jpg")))
                .country("Unknown")
                .city("Unknown")
                .build();
        var savedUser = userRepository.save(user);
        String jvtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jvtToken);
        emailSender.sendEmailConfirmation(savedUser.getEmail(), savedUser.getNickname(), jvtToken);
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isNicknameTaken(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }

    public String confirmEmail(String token) {
        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null && !jwtService.isTokenExpired(token)) {
            user.setActivity(Boolean.TRUE);
            userRepository.save(user);
            return "Confirmed";
        }
        return "Confirmation failed";
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            return AuthenticationResponse.builder()
                    .build();
        }

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User with email " + request.getEmail() + " is not found"));
        var jvtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jvtToken);
        return AuthenticationResponse.builder()
                .token(jvtToken)
                .build();
    }

    public String forgotPassword(Map<String, String> request) {
        User user = userRepository.findByEmail(request.get("email")).orElse(null);
        if (user != null) {
            String jvtToken = jwtService.generateToken(user);
            saveUserToken(user, jvtToken);
            emailSender.sendResetPasswordConfirmation(user.getEmail(), user.getNickname(), jvtToken);
            return "A password reset link has been sent to your email address";
        }
        return "There are no registered users with the email address " + request.get("email");
    }

    public boolean resetPassword(String token, String newPassword) {
        User user = userRepository.findByEmail(jwtService.extractEmail(token)).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) return;
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(User user, String jvtToken) {
        var token = Token.builder()
                .user(user)
                .token(jvtToken)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }
}
