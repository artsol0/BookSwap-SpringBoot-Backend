package com.artsolo.bookswap.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
