package com.artsolo.bookswap.controllers.user;

import com.artsolo.bookswap.controllers.responses.ErrorDescription;
import com.artsolo.bookswap.controllers.responses.ErrorResponse;
import com.artsolo.bookswap.controllers.responses.MessageResponse;
import com.artsolo.bookswap.controllers.responses.SuccessResponse;
import com.artsolo.bookswap.models.User;
import com.artsolo.bookswap.models.enums.Role;
import com.artsolo.bookswap.services.JwtService;
import com.artsolo.bookswap.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @GetMapping("/get/current")
    public ResponseEntity<?> getCurrentUser(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        return ResponseEntity.ok().body(SuccessResponse.builder().data(userService.getUserResponse(user)).build());
    }

    @GetMapping("/get/current-id")
    public ResponseEntity<?> getCurrentUserId(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        return ResponseEntity.ok().body(SuccessResponse.builder().data(user.getId()).build());
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
    public ResponseEntity<?> changePhoto(@RequestParam("photo") MultipartFile photo, Principal currentUser) {
        userService.changeUserPhoto(photo, currentUser);
        return ResponseEntity.ok().body(MessageResponse.builder().message("Photo changed successfully").build());
    }

    @PutMapping("/change-activity/{id}")
    public ResponseEntity<?> changeActivityById(@PathVariable Long id) {
        if (userService.changeUserActivity(userService.getUserById(id))) {
            return ResponseEntity.ok().body(MessageResponse.builder().message("User is active").build());
        }
        return ResponseEntity.ok().body(MessageResponse.builder().message("User is inactive").build());
    }

    @PutMapping("set-role/{id}")
    public ResponseEntity<?> setUserRole(@PathVariable Long id, @RequestBody Map<String, String> request) {
        if (request.get("role") != null) {
            if (userService.setUserRole(userService.getUserById(id), Role.valueOf(request.get("role")))) {
                return ResponseEntity.ok().body(MessageResponse.builder().message("User role changed").build());
            }
            return ResponseEntity.ok().body(MessageResponse.builder().message("User role not changed").build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), "Bad request")).build());
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, Principal currentUser) {
        if (request.get("current_password") != null && request.get("new_password") != null) {
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
