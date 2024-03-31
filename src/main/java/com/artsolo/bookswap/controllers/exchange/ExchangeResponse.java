package com.artsolo.bookswap.controllers.exchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeResponse {
    private Long id;
    private String initiator;
    private String recipient;
    private String book;
    private Boolean confirmed;
}
