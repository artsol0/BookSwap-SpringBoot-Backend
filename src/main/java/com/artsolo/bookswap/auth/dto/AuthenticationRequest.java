package com.artsolo.bookswap.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {
    @NotNull(message = "Email is mandatory")
    @Email(message = "Email address must be valid")
    String email;
    @NotNull(message = "Password is mandatory")
    String password;
}
