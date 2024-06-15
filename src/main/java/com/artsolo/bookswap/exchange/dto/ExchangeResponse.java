package com.artsolo.bookswap.exchange.dto;

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
    private Long initiatorId;
    private String initiator;
    private Long recipientId;
    private String recipient;
    private Long bookId;
    private String book;
    private Boolean confirmed;
}
