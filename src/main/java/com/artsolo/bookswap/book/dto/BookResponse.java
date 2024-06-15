package com.artsolo.bookswap.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {
    private Long id;
    private Long ownerId;
    private String title;
    private String author;
    private String description;
    private List<String> genres;
    private String quality;
    private String status;
    private String language;
    private byte[] photo;
}
