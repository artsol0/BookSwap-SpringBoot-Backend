package com.artsolo.bookswap.controllers;

import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.services.JwtService;
import com.artsolo.bookswap.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;


    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                return ResponseEntity.ok().body(User.builder()
                        .id(user.getId())
                        .nickname(user.getNickname())
                        .email(user.getEmail())
                        .points(user.getPoints())
                        .country(user.getCountry())
                        .city(user.getCity())
                        .registrationDate(user.getRegistrationDate())
                        .activity(user.getActivity())
                        .role(user.getRole())
                        .build());
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error occurred during getting user by id", e);
            return new ResponseEntity<String>("Something want wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/activity/{id}")
    public ResponseEntity<String> changeUserActivityById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                return ResponseEntity.ok(userService.changeUserActivity(user));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error occurred during getting user by id", e);
            return new ResponseEntity<String>("Something want wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> request, Principal currentUser) {
        try {
            return ResponseEntity.ok(userService.changeUserPassword(request, currentUser));
        }catch (Exception e) {
            logger.error("Error occurred during getting user by id", e);
            return new ResponseEntity<String>("Something want wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
