package com.artsolo.bookswap.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "revives")
public class Revive {
    @EmbeddedId
    private CompositeKey reviveId;
    private Integer rating;
    private String revive;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("book_id")
    @JoinColumn(name = "book_id")
    private Book book;
}
