package com.artsolo.bookswap.controllers.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBookRequest {
    private String title;
    private String author;
    private List<Long> genreIds;
    private Long qualityId;
    private Long statusId;
    private Long languageId;
    private MultipartFile photo;
}
