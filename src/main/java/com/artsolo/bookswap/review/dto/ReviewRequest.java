package com.artsolo.bookswap.review.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

    @NotNull(message = "Rating can't be null")
    @Max(value = 5, message = "Max rating value is 5")
    @Min(value = 1, message = "Min rating value is 1")
    private Integer rating;

    @NotBlank(message = "Review is mandatory")
    @NotNull(message = "Review can't be null")
    @Size(max = 1000, message = "Review cannot contain more than 1000 characters")
    private String review;

}
