package com.artsolo.bookswap.repositoryes;

import com.artsolo.bookswap.token.Token;
import com.artsolo.bookswap.token.TokenRepository;
import com.artsolo.bookswap.user.User;
import com.artsolo.bookswap.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import java.util.Optional;

@DataJpaTest
class TokenRepositoryTest {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    private final User firstUser = new User();
    private final User secondUser = new User();
    private final User thirdUser = new User();
    private final User fourthUser = new User();

    private final Token validToken = Token.builder()
            .token("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtZWdhY2oyMzEkALBA")
            .expired(false)
            .revoked(false)
            .user(firstUser)
            .build();

    private final Token secondValidToken = Token.builder()
            .token("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJic2RmYmY0NEB5b3BtYW")
            .expired(false)
            .revoked(false)
            .user(secondUser)
            .build();

    private final Token expiredToken = Token.builder()
            .token("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZ2RoZHI0NUB5b3BtY")
            .expired(true)
            .revoked(false)
            .user(thirdUser)
            .build();

    private final Token revokedToken = Token.builder()
            .token("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtZWdhY2oyMzE0QHlvc")
            .expired(false)
            .revoked(true)
            .user(fourthUser)
            .build();

    @BeforeEach
    void setUp() {
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        userRepository.save(thirdUser);
        userRepository.save(fourthUser);

        tokenRepository.save(validToken);
        tokenRepository.save(secondValidToken);
        tokenRepository.save(expiredToken);
        tokenRepository.save(revokedToken);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        tokenRepository.deleteAll();
    }

    @Test
    void foundFirstUserValidTokens() {
        List<Token> tokens = tokenRepository.findAllValidTokensByUser(firstUser.getId());
        assertThat(tokens.size()).isEqualTo(1);
        tokens.forEach(token -> assertThat(token.getUser().getId()).isEqualTo(firstUser.getId()));
    }

    @Test
    void foundThirdUserNoRevokedTokens() {
        List<Token> tokens = tokenRepository.findAllValidTokensByUser(thirdUser.getId());
        assertThat(tokens.size()).isEqualTo(1);
        tokens.forEach(token -> assertThat(token.getUser().getId()).isEqualTo(thirdUser.getId()));
        tokens.forEach(token -> assertThat(token.isRevoked()).isFalse());
        tokens.forEach(token -> assertThat(token.isExpired()).isTrue());
    }

    @Test
    void foundThirdUserNoExpiredTokens() {
        List<Token> tokens = tokenRepository.findAllValidTokensByUser(fourthUser.getId());
        assertThat(tokens.size()).isEqualTo(1);
        tokens.forEach(token -> assertThat(token.getUser().getId()).isEqualTo(fourthUser.getId()));
        tokens.forEach(token -> assertThat(token.isRevoked()).isTrue());
        tokens.forEach(token -> assertThat(token.isExpired()).isFalse());
    }

    @Test
    void findByToken() {
        Optional<Token> token = tokenRepository.findByToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtZWdhY2oyMzEkALBA");
        assertThat(token.isPresent()).isTrue();
        assertThat(token.get().getToken()).isEqualTo("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtZWdhY2oyMzEkALBA");
        assertThat(token.get().getUser().getId()).isEqualTo(firstUser.getId());
    }
}