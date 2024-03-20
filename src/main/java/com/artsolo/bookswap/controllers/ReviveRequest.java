package com.artsolo.bookswap.controllers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviveRequest {
    private Long bookId;
    private Integer rating;
    private String revive;
}
