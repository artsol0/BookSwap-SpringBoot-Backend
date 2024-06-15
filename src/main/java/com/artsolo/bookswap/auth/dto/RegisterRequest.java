package com.artsolo.bookswap.auth.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Nickname is mandatory")
    @NotNull(message = "Nickname can't be null")
    @Size(max = 35, message = "Nickname cannot contain more than 35 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Nickname cannot contain special characters")
    private String nickname;

    @NotBlank(message = "Email is mandatory")
    @NotNull(message = "Email can't be null")
    @Email(message = "Email address must be valid")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @NotNull(message = "Password can't be null")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern.List({
            @Pattern(regexp = "(?=.*[A-Z]).+", message = "Password must contain at least one uppercase letter"),
            @Pattern(regexp = "(?=.*[a-z]).+", message = "Password must contain at least one lowercase letter"),
            @Pattern(regexp = "(?=.*\\d).+", message = "Password must contain at least one digit"),
            @Pattern(regexp = "(?=.*[!@#$%^&*]).+", message = "Password must contain at least one special character")
    })
    private String password;

}
