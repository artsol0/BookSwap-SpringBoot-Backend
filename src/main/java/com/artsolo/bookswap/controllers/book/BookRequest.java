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
public class BookRequest {
    private String title;
    private String author;
    private List<Long> genreIds;
    private Long qualityId;
    private Long statusId;
    private Long languageId;
}