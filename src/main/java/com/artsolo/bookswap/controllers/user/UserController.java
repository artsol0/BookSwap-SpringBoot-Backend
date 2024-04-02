package com.artsolo.bookswap.controllers.user;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.services.JwtService;
import com.artsolo.bookswap.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserResponse(userService.getUserById(id));
        return ResponseEntity.ok().body(SuccessResponse.builder().data(userResponse).build());
    }

    @PutMapping("/change-location")
    public ResponseEntity<?> changeLocation(@RequestBody Map<String, String> request, Principal currentUser) {
        if (request.get("country") != null && request.get("city") != null) {
            userService.changeUserLocation(request, currentUser);
            return ResponseEntity.ok().body(MessageResponse.builder().message("Location changed successfully").build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
    }

    @PutMapping("/change-photo")
    public ResponseEntity<?> changePhoto(@RequestPart("photo") MultipartFile photo, Principal currentUser) {
        userService.changeUserPhoto(photo, currentUser);
        return ResponseEntity.ok().body(MessageResponse.builder().message("Photo changed successfully").build());
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, Principal currentUser) {
        if (request.get("old-password") != null && request.get("new-password") != null) {
            String result = userService.changeUserPassword(request, currentUser);
            if (result.contains("successfully")) {
                return ResponseEntity.ok().body(MessageResponse.builder().message(result).build());
            }
            return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                    HttpStatus.BAD_REQUEST.value(), result)).build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
    }
}
