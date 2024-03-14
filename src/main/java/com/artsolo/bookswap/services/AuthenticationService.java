package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.AuthenticationRequest;
import com.artsolo.bookswap.controllers.AuthenticationResponse;
import com.artsolo.bookswap.controllers.RegisterRequest;
import com.artsolo.bookswap.models.Role;
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

import java.util.Date;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, TokenRepository tokenRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isEmpty()) {
            var user = User.builder()
                    .nickname(request.getNickname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.READER)
                    .activity(true)
                    .registrationDate(new Date())
                    .points(0)
                    .build();
            var savedUser = userRepository.save(user);
            var jvtToken = jwtService.generateToken(user);
            saveUserToken(savedUser, jvtToken);
            return AuthenticationResponse.builder()
                    .token(jvtToken)
                    .message("Successes")
                    .build();
        }
        return AuthenticationResponse.builder()
                .message("Email " + request.getEmail() + " is already taken")
                .build();
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
                    .message("Invalid credentials")
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
                .message("Successes")
                .build();
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
