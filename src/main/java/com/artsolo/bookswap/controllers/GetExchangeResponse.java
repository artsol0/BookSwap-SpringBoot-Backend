package com.artsolo.bookswap.controllers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetExchangeResponse {
    private Long id;
    private String initiator;
    private String recipient;
    private String book;
    private Boolean confirmed;
}
