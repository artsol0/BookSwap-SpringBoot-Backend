package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.user.UserResponse;
import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.repositoryes.UserRepository;
import lombok.extern.slf4j.Slf4j;
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
        return userRepository.findById(id).orElseThrow(() -> new NoDataFoundException("User", id));
    }

    public UserResponse getUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .points(user.getPoints())
                .country(user.getCountry())
                .city(user.getCity())
                .registrationDate(user.getRegistrationDate())
                .activity(user.getActivity())
                .role(user.getRole())
                .photo(user.getPhoto())
                .build();
    }

    public boolean changeUserActivity(User user) {
        user.setActivity(!user.getActivity());
        return user.getActivity();
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
