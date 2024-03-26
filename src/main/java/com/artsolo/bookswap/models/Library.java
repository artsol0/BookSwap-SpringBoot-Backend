package com.artsolo.bookswap.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Library")
public class Library {
    @EmbeddedId
    private CompositeKey libraryId;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @MapsId("book_id")
    @JoinColumn(name = "book_id")
    private Book book;
}
