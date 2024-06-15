package com.artsolo.bookswap.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private static final String email = "tester234@gmail.com";
    private static final String nickname = "tester";

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .nickname(nickname)
                .email(email)
                .build();
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void userFoundByEmail() {
        boolean isUserFound = userRepository.findByEmail(email).isPresent();
        assertThat(isUserFound).isTrue();
    }

    @Test
    void userNotFoundByEmail() {
        boolean isUserFound = userRepository.findByEmail("prefix" + email).isPresent();
        assertThat(isUserFound).isFalse();
    }

    @Test
    void userFoundByNickname() {
        boolean isUserFound = userRepository.findByNickname(nickname).isPresent();
        assertThat(isUserFound).isTrue();
    }

    @Test
    void userNotFoundByNickname() {
        boolean isUserFound = userRepository.findByNickname("prefix" + nickname).isPresent();
        assertThat(isUserFound).isFalse();
    }

}