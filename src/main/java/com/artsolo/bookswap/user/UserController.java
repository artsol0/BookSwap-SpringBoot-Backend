package com.artsolo.bookswap.user;

import com.artsolo.bookswap.responses.ErrorDescription;
import com.artsolo.bookswap.responses.ErrorResponse;
import com.artsolo.bookswap.responses.MessageResponse;
import com.artsolo.bookswap.responses.SuccessResponse;
import com.artsolo.bookswap.enums.Role;
import com.artsolo.bookswap.user.dto.LocationChangeRequest;
import com.artsolo.bookswap.user.dto.PasswordChangeRequest;
import com.artsolo.bookswap.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<SuccessResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserResponse(userService.getUserById(id));
        return ResponseEntity.ok().body(new SuccessResponse<>(userResponse));
    }

    @GetMapping("/get/current")
    public ResponseEntity<SuccessResponse<UserResponse>> getCurrentUser(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        return ResponseEntity.ok().body(new SuccessResponse<>(userService.getUserResponse(user)));
    }

    @GetMapping("/get/current-id")
    public ResponseEntity<SuccessResponse<Long>> getCurrentUserId(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        return ResponseEntity.ok().body(new SuccessResponse<>(user.getId()));
    }

    @PutMapping("/change-location")
    public ResponseEntity<MessageResponse> changeLocation(@RequestBody @Valid LocationChangeRequest request, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        userService.changeUserLocation(request, user);
        return ResponseEntity.ok().body(MessageResponse.builder().message("Location changed successfully").build());
    }

    @PutMapping("/change-photo")
    public ResponseEntity<MessageResponse> changePhoto(@RequestParam("photo") MultipartFile photo, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        userService.changeUserPhoto(photo, user);
        return ResponseEntity.ok().body(MessageResponse.builder().message("Photo changed successfully").build());
    }

    @PutMapping("/change-activity/{id}")
    public ResponseEntity<MessageResponse> changeActivityById(@PathVariable Long id) {
        if (userService.changeUserActivity(userService.getUserById(id))) {
            return ResponseEntity.ok().body(MessageResponse.builder().message("User is active").build());
        }
        return ResponseEntity.ok().body(MessageResponse.builder().message("User is inactive").build());
    }

    @PutMapping("/{id}/set-role")
    public ResponseEntity<MessageResponse> setUserRole(@PathVariable Long id, @RequestParam("role") String role) {
        if (userService.setUserRole(userService.getUserById(id), Role.valueOf(role))) {
            return ResponseEntity.ok().body(MessageResponse.builder().message("User role changed").build());
        }
        return ResponseEntity.ok().body(MessageResponse.builder().message("User role not changed").build());
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid PasswordChangeRequest request, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        String result = userService.changeUserPassword(request, user);
        if (result.contains("successfully")) {
            return ResponseEntity.ok().body(MessageResponse.builder().message(result).build());
        }
        return ResponseEntity.badRequest().body(ErrorResponse.builder().error(new ErrorDescription(
                HttpStatus.BAD_REQUEST.value(), result)).build());
    }
}
