package com.artsolo.bookswap.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Blob;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id", nullable = false)
    private Long id;
    private String title;
    private String author;

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;

    @ManyToOne
    @JoinColumn(name = "quality_id")
    private Quality quality;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    @Lob
    @Column(columnDefinition="BLOB")
    private byte[] photo;

    @OneToMany(mappedBy = "book", orphanRemoval = true)
    private List<Review> reviews;
    @OneToMany(mappedBy = "book", orphanRemoval = true)
    private List<Wishlist> wishlist;
    @OneToMany(mappedBy = "book", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Exchange> exchanges;
    @OneToMany(mappedBy = "book", orphanRemoval = true)
    private List<Note> notes;

}
