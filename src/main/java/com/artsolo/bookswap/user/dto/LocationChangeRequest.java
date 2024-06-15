package com.artsolo.bookswap.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationChangeRequest {
    @NotBlank(message = "Country is mandatory")
    @NotNull(message = "Country can't be null")
    private String country;

    @NotBlank(message = "City is mandatory")
    @NotNull(message = "City can't be null")
    private String city;
}
