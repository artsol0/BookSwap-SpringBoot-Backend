package com.artsolo.bookswap.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookAdditionalInfo {
    private boolean isUserBookOwner;
    private boolean isBookInWishlist;
    private boolean isBookInExchange;
}
