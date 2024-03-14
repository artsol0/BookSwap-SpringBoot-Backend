package com.artsolo.bookswap.services;

import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.repositoryes.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        user.setPoints(0);
        return userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public boolean deleteUser(User user) {
        userRepository.delete(user);
        return userRepository.findById(user.getId()).isEmpty();
    }
}
