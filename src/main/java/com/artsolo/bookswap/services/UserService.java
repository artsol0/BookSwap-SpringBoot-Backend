package com.artsolo.bookswap.services;

import com.artsolo.bookswap.controllers.user.LocationChangeRequest;
import com.artsolo.bookswap.controllers.user.PasswordChangeRequest;
import com.artsolo.bookswap.controllers.user.UserResponse;
import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.models.enums.Role;
import com.artsolo.bookswap.repositoryes.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        user = userRepository.save(user);
        return user.getActivity();
    }

    public boolean setUserRole(User user, Role role) {
        user.setRole(role);
        user = userRepository.save(user);
        return user.getRole().equals(role);
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

    public void changeUserLocation(LocationChangeRequest request, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        user.setCountry(request.getCountry());
        user.setCity(request.getCity());
        userRepository.save(user);
    }

    @Transactional
    public void increaseUserPoints(int number, User user) {
        user.setPoints(user.getPoints() + number);
        userRepository.save(user);
    }

    @Transactional
    public void decreaseUserPoints(int number, User user) {
        user.setPoints(user.getPoints() - number);
        userRepository.save(user);
    }

    public String changeUserPassword(PasswordChangeRequest request, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        if(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            if (!passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);
                return "Password changed successfully";
            }
            return "New password must not match previous";
        }
        return "Current password not confirmed";
    }
}
