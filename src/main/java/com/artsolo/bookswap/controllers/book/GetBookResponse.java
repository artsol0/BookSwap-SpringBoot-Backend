package com.artsolo.bookswap.controllers.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetBookResponse {
    private Long id;
    private String title;
    private String author;
    private List<String> genres;
    private String quality;
    private String status;
    private String language;
    private byte[] photo;
}
