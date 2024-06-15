package com.artsolo.bookswap.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {
    private Long userId;
    private Long bookId;
    private String nickname;
    private byte[] userPhoto;
    private Integer rating;
    private String review;
}
