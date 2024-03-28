package com.artsolo.bookswap.controllers.user;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.services.JwtService;
import com.artsolo.bookswap.services.UserService;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
                GetUserResponse getUserResponse = GetUserResponse.builder()
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
                return ResponseEntity.ok().body(new SuccessResponse<>(getUserResponse));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.NOT_FOUND.value(),
                    "User with id '" + id + "' not found")
            ));
        } catch (Exception e) {
            logger.error("Error occurred during getting user by id", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }

    @PutMapping("/change-location")
    public ResponseEntity<?> changeLocation(@RequestBody Map<String, String> request, Principal currentUser) {
        try {
            if (request.get("country") != null && request.get("city") != null) {
                userService.changeUserLocation(request, currentUser);
                return ResponseEntity.ok().body(new SuccessResponse<>("Location changed successfully"));
            }
            return ResponseEntity.badRequest().body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad request")
            ));
        } catch (Exception e) {
            logger.error("Error occurred during changing user password", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }

    @PutMapping("/change-photo")
    public ResponseEntity<?> changePhoto(@RequestPart("photo") MultipartFile photo, Principal currentUser) {
        try {
            userService.changeUserPhoto(photo, currentUser);
            return ResponseEntity.ok().body(new SuccessResponse<>("Photo changed successfully"));
        } catch (Exception e) {
            logger.error("Error occurred during changing user password", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, Principal currentUser) {
        try {
            if (request.get("old-password") != null && request.get("new-password") != null) {
                String result = userService.changeUserPassword(request, currentUser);
                if (result.contains("successfully")) {
                    return ResponseEntity.ok().body(new MessageResponse(result));
                }
                return ResponseEntity.badRequest().body(new ErrorResponse(new ErrorDescription(
                        HttpStatus.BAD_REQUEST.value(),
                        result)
                ));
            }
            return ResponseEntity.badRequest().body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad request")
            ));
        } catch (Exception e) {
            logger.error("Error occurred during changing user password", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(new ErrorDescription(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal server error")
            ));
        }
    }
}
