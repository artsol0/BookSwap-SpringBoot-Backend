package com.artsolo.bookswap.user;

import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.enums.Role;
import com.artsolo.bookswap.user.dto.LocationChangeRequest;
import com.artsolo.bookswap.user.dto.PasswordChangeRequest;
import com.artsolo.bookswap.user.dto.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    public void changeUserPhoto(MultipartFile photo, User user) {
        try {
            byte[] newPhoto = photo.getBytes();
            user.setPhoto(newPhoto);
            userRepository.save(user);
        } catch (IOException e) {
            log.error("Error occurred during changing user photo: {}", e.getMessage());
        }
    }

    public void changeUserLocation(LocationChangeRequest request, User user) {
        user.setCountry(request.getCountry());
        user.setCity(request.getCity());
        userRepository.save(user);
    }

    public void increaseUserPoints(int number, User user) {
        if (number < 0) throw new IllegalArgumentException("Number can't be negative");
        user.setPoints(user.getPoints() + number);
        userRepository.save(user);
    }

    public void decreaseUserPoints(int number, User user) {
        if (number < 0) throw new IllegalArgumentException("Number can't be negative");
        user.setPoints(user.getPoints() - number);
        userRepository.save(user);
    }

    public String changeUserPassword(PasswordChangeRequest request, User user) {
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
