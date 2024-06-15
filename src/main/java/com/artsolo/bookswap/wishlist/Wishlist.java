package com.artsolo.bookswap.wishlist;

import com.artsolo.bookswap.book.Book;
import com.artsolo.bookswap.compositekey.CompositeKey;
import com.artsolo.bookswap.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wishlist")
public class Wishlist {
    @EmbeddedId
    private CompositeKey wishlistId;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("book_id")
    @JoinColumn(name = "book_id")
    private Book book;
}
