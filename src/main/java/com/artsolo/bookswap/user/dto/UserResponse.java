package com.artsolo.bookswap.user.dto;

import com.artsolo.bookswap.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String nickname;
    private String email;
    private Integer points;
    private Boolean activity;
    private String country;
    private String city;
    private Role role;
    private LocalDate registrationDate;
    private byte[] photo;
}
