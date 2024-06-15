package com.artsolo.bookswap.book;

import com.artsolo.bookswap.attributes.genre.Genre;
import com.artsolo.bookswap.attributes.language.Language;
import com.artsolo.bookswap.attributes.quality.Quality;
import com.artsolo.bookswap.attributes.status.Status;
import com.artsolo.bookswap.exchange.Exchange;
import com.artsolo.bookswap.note.Note;
import com.artsolo.bookswap.review.Review;
import com.artsolo.bookswap.user.User;
import com.artsolo.bookswap.wishlist.Wishlist;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(length = 50)
    private String title;
    @Column(length = 50)
    private String author;
    @Column(length = 1000)
    private String description;

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quality_id")
    private Quality quality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    @Lob
    @Column(columnDefinition="LONGBLOB")
    private byte[] photo;

    @OneToMany(mappedBy = "book", orphanRemoval = true)
    private List<Review> reviews;
    @OneToMany(mappedBy = "book", orphanRemoval = true)
    private List<Wishlist> wishlist;
    @OneToMany(mappedBy = "book", orphanRemoval = true)
    private List<Exchange> exchanges;
    @OneToMany(mappedBy = "book", orphanRemoval = true)
    private List<Note> notes;

}
