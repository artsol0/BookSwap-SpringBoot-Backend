package com.artsolo.bookswap.note.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoteResponse {
    private Long id;
    private String country;
    private String city;
    private LocalDate date;
}
