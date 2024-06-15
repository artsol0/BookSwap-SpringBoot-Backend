package com.artsolo.bookswap.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookRequest {

    @NotBlank(message = "Title is mandatory")
    @NotNull(message = "Title can't be null")
    @Size(max = 50, message = "Title cannot contain more than 50 characters")
    private String title;

    @NotBlank(message = "Author is mandatory")
    @NotNull(message = "Author can't be null")
    @Size(max = 50, message = "Author cannot contain more than 50 characters")
    private String author;

    @Size(max = 1000, message = "Description cannot contain more than 1000 characters")
    private String description;

    @NotEmpty(message = "Book must at list contain one genre")
    private List<Long> genreIds;

    @NotNull(message = "Quality can't be null")
    private Long qualityId;

    @NotNull(message = "Status can't be null")
    private Long statusId;

    @NotNull(message = "Photo can't be null")
    private Long languageId;

}
