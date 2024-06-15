package com.artsolo.bookswap.review;

import com.artsolo.bookswap.book.Book;
import com.artsolo.bookswap.compositekey.CompositeKey;
import com.artsolo.bookswap.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "reviews")
public class Review {
    @EmbeddedId
    private CompositeKey reviewId;
    private Integer rating;
    @Column(length = 1000)
    private String review;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("book_id")
    @JoinColumn(name = "book_id")
    private Book book;
}
