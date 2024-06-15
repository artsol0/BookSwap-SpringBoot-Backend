package com.artsolo.bookswap.auth;

import com.artsolo.bookswap.auth.dto.AuthenticationRequest;
import com.artsolo.bookswap.auth.dto.AuthenticationResponse;
import com.artsolo.bookswap.auth.dto.RegisterRequest;
import com.artsolo.bookswap.enums.Role;
import com.artsolo.bookswap.email.EmailSender;
import com.artsolo.bookswap.token.JwtService;
import com.artsolo.bookswap.token.Token;
import com.artsolo.bookswap.user.User;
import com.artsolo.bookswap.token.TokenRepository;
import com.artsolo.bookswap.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailSender emailSender;

    public void register(RegisterRequest request) {
        User user = getUserFromRegisterRequest(request).orElseThrow(() -> new RuntimeException("Error occurred while getting User from request"));
        User savedUser = userRepository.save(user);
        String jvtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jvtToken);
        emailSender.sendEmailConfirmation(savedUser.getEmail(), savedUser.getNickname(), jvtToken);
    }

    public Optional<User> getUserFromRegisterRequest(RegisterRequest request) {
        try {
            return Optional.of(User.builder()
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
                    .build());
        } catch (IOException e) {
            log.error("Error occurred while reading default user photo: {}", e.getMessage());
            return Optional.empty();
        }
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
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            return AuthenticationResponse.builder().build();
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                        new UsernameNotFoundException("User with email " + request.getEmail() + " is not found"));

        String jvtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jvtToken);
        return AuthenticationResponse.builder().token(jvtToken).build();
    }

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            String jvtToken = jwtService.generateToken(user);
            saveUserToken(user, jvtToken);
            emailSender.sendResetPasswordConfirmation(user.getEmail(), user.getNickname(), jvtToken);
            return "A password reset link has been sent to your email address";
        }
        return "There are no registered users with the email address " + email;
    }

    public boolean resetPassword(String token, String newPassword) {
        User user = userRepository.findByEmail(jwtService.extractEmail(token)).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        user = userRepository.save(user);
        return passwordEncoder.matches(newPassword, user.getPassword());
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) return;
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(User user, String jvtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jvtToken)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }
}
