package com.artsolo.bookswap.services;

import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.repositoryes.UserRepository;
import org.hibernate.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public String changeUserActivity(User user) {
        if (user != null) {
            if (user.isActivity()) {
                user.setActivity(false);
                userRepository.save(user);
                return "User " + user.getNickname() + " with id " + user.getId() + " was banned";
            }
            user.setActivity(true);
            userRepository.save(user);
            return "User " + user.getNickname() + " with id " + user.getId() + " was unbanned";
        }
        throw new NullPointerException("User cannot be null");
    }
}
