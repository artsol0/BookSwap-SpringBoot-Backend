package com.artsolo.bookswap.services;

import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.repositoryes.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public String changeUserActivity(User user) {
        if (user != null) {
            if (user.getActivity()) {
                user.setActivity(Boolean.FALSE);
                userRepository.save(user);
                return "User " + user.getNickname() + " with id " + user.getId() + " was banned";
            }
            user.setActivity(Boolean.TRUE);
            userRepository.save(user);
            return "User " + user.getNickname() + " with id " + user.getId() + " was unbanned";
        }
        throw new NullPointerException("User cannot be null");
    }

    public void changeUserPhoto(MultipartFile photo, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        try {
            byte[] newPhoto = photo.getBytes();
            user.setPhoto(newPhoto);
            userRepository.save(user);
        } catch (IOException e) {
            log.error("Error occurred during changing user photo", e);
        }
    }

    public void changeUserLocation(Map<String, String> request, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        user.setCountry(request.get("country"));
        user.setCity(request.get("city"));
        userRepository.save(user);
    }

    public void increaseUserPoints(int number, User user) {
        user.setPoints(user.getPoints() + number);
        userRepository.save(user);
    }

    public void decreaseUserPoints(int number, User user) {
        user.setPoints(Math.max(user.getPoints() - number, 0));
        userRepository.save(user);
    }

    public String changeUserPassword(Map<String, String> request, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        if(passwordEncoder.matches(request.get("old-password"), user.getPassword())) {
            if (!passwordEncoder.matches(request.get("new-password"), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(request.get("new-password")));
                userRepository.save(user);
                return "Password changed successfully";
            }
            return "New password must not match previous";
        }
        return "Password not confirmed";
    }
}
